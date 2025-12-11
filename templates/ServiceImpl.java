package {{ module|lower }}.service;

import {{ module|lower }}.dto.{{ module }}DTO;
import {{ module|lower }}.entity.{{ module }}Entity;
import {{ module|lower }}.mapper.{{ module }}Mapper;
import {{ module|lower }}.repository.{{ module }}Repository;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@Service
public class {{ module }}ServiceImpl implements {{ module }}Service {

    private final {{ module }}Repository repository;

    public {{ module }}ServiceImpl({{ module }}Repository repository) {
        this.repository = repository;
    }

    @Override
    public {{ module }}DTO create({{ module }}DTO dto) {
        {{ module }}Entity entity = {{ module }}Mapper.toEntity(dto);
        return {{ module }}Mapper.toDTO(repository.save(entity));
    }

    @Override
    public {{ module }}DTO getById(Long id) {
        return repository.findById(id)
                .map({{ module }}Mapper::toDTO)
                .orElse(null);
    }

    @Override
    public List<{{ module }}DTO> getAll() {
        return repository.findAll()
                .stream()
                .map({{ module }}Mapper::toDTO)
                .collect(Collectors.toList());
    }

    @Override
    public {{ module }}DTO update(Long id, {{ module }}DTO dto) {
        return repository.findById(id).map(entity -> {
            {% for f in fields.split(',') %}
            entity.set{{ f.split(':')[0]|capitalize }}(dto.get{{ f.split(':')[0]|capitalize }}());
            {% endfor %}
            return {{ module }}Mapper.toDTO(repository.save(entity));
        }).orElse(null);
    }

    @Override
    public void delete(Long id) {
        repository.deleteById(id);
    }
}
