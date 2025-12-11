from flask import Flask, request, jsonify, send_file
import os
from jinja2 import Environment, FileSystemLoader
import re
import tempfile
import zipfile
import shutil
import json
from io import BytesIO
from flask_cors import CORS

app = Flask(__name__)
CORS(app)
env = Environment(loader=FileSystemLoader('templates'))

# Folder mapping for each template
FOLDER_MAP = {
    'Entity.java': 'entity',
    'DTO.java': 'dto',
    'Mapper.java': 'mapper',
    'Repository.java': 'repository',
    'Service.java': 'service',
    'ServiceImpl.java': 'service/impl',
    'Controller.java': 'controller',
    'TestController.java': 'tests/controller',
    'TestService.java': 'tests/service'
}

# Sanitize Java module names
def sanitize_module_name(name: str) -> str:
    name = re.sub(r'\W|^(?=\d)', '', name)
    if not name:
        name = "Module"
    return name[0].upper() + name[1:]

# Sanitize field names
def sanitize_field_name(name: str) -> str:
    name = re.sub(r'\W|^(?=\d)', '', name)
    if not name:
        name = "field"
    return name

# Convert package name to folder path
def package_to_path(base_path, package):
    parts = package.split('.')
    return os.path.join(base_path, *parts)

def generate_for_path(project_path, base_package, modules, generateDTO=True, generateMapper=True, generateTests=True):
    """Generate module files into the provided project path."""
    src_base = package_to_path(os.path.join(project_path, 'src', 'main', 'java'), base_package)
    os.makedirs(src_base, exist_ok=True)

    for mod in modules:
        module_raw = mod.get('name', '')
        if not module_raw:
            continue
        module = sanitize_module_name(module_raw)
        fields_raw = mod.get('fields', '')
        fields = []
        for f in fields_raw.split(','):
            if ':' in f:
                fname, ftype = f.split(':', 1)
                fields.append(f"{sanitize_field_name(fname)}:{ftype}")
        fields_str = ','.join(fields)

        module_path = os.path.join(src_base, module.lower())
        os.makedirs(module_path, exist_ok=True)

        templates = ['Controller.java', 'Service.java', 'ServiceImpl.java',
                     'Repository.java', 'Entity.java']
        if generateDTO:
            templates.append('DTO.java')
        if generateMapper:
            templates.append('Mapper.java')
        if generateTests:
            templates += ['TestController.java', 'TestService.java']

        for tpl_name in templates:
            tpl = env.get_template(tpl_name)
            content = tpl.render(module=module, fields=fields_str)

            folder_name = FOLDER_MAP.get(tpl_name, '')
            final_path = os.path.join(module_path, folder_name)
            os.makedirs(final_path, exist_ok=True)

            filename = f"{module}{tpl_name}" if tpl_name.endswith(".java") else tpl_name
            with open(os.path.join(final_path, filename), 'w') as f:
                f.write(content)


@app.route('/generate', methods=['POST'])
def generate_modules():
    """
    Accepts multipart/form-data with:
      - projectZip: Spring Initializr zip file (file)
      - basePackage: e.g., com.example.demo
      - modules: JSON string list of {name, fields}
      - generateDTO, generateMapper, generateTests: optional bools
    Returns: merged project zip with generated templates.
    """
    if 'projectZip' not in request.files:
        return jsonify({"error": "projectZip file is required"}), 400

    project_zip = request.files['projectZip']
    base_package = request.form.get('basePackage', '').strip()
    modules_raw = request.form.get('modules', '[]')

    if not base_package:
        return jsonify({"error": "basePackage is required"}), 400

    try:
        modules = json.loads(modules_raw)
        if not isinstance(modules, list):
            raise ValueError
    except Exception:
        return jsonify({"error": "modules must be a JSON array"}), 400

    generateDTO = request.form.get('generateDTO', 'true').lower() == 'true'
    generateMapper = request.form.get('generateMapper', 'true').lower() == 'true'
    generateTests = request.form.get('generateTests', 'true').lower() == 'true'

    temp_dir = tempfile.mkdtemp(prefix="springgen_")
    extracted_path = None
    try:
        zip_path = os.path.join(temp_dir, "project.zip")
        project_zip.save(zip_path)

        with zipfile.ZipFile(zip_path, 'r') as zf:
            zf.extractall(temp_dir)

        # Spring Initializr zips typically contain a single root folder
        entries = [os.path.join(temp_dir, p) for p in os.listdir(temp_dir) if os.path.isdir(os.path.join(temp_dir, p))]
        if entries:
            extracted_path = entries[0]
        else:
            extracted_path = temp_dir

        generate_for_path(
            extracted_path,
            base_package,
            modules,
            generateDTO=generateDTO,
            generateMapper=generateMapper,
            generateTests=generateTests
        )

        # Re-zip the project
        out_buffer = BytesIO()
        with zipfile.ZipFile(out_buffer, 'w', zipfile.ZIP_DEFLATED) as zf:
            for root, dirs, files in os.walk(extracted_path):
                for file in files:
                    abs_path = os.path.join(root, file)
                    rel_path = os.path.relpath(abs_path, extracted_path)
                    zf.write(abs_path, rel_path)
        out_buffer.seek(0)

        download_name = f"{os.path.basename(extracted_path) or 'project'}-with-modules.zip"
        return send_file(
            out_buffer,
            mimetype='application/zip',
            as_attachment=True,
            download_name=download_name
        )
    finally:
        shutil.rmtree(temp_dir, ignore_errors=True)

if __name__ == '__main__':
    app.run(host='0.0.0.0', port=5000)
