package {{ module|lower }}.controller;

import {{ module|lower }}.dto.{{ module }}DTO;
import {{ module|lower }}.service.{{ module }}Service;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/{{ module|lower }}")
public class {{ module }}Controller {

    private final {{ module }}Service service;

    public {{ module }}Controller({{ module }}Service service) {
        this.service = service;
    }

    @PostMapping
    public {{ module }}DTO create(@RequestBody {{ module }}DTO dto) {
        return service.create(dto);
    }

    @GetMapping("/{id}")
    public {{ module }}DTO getById(@PathVariable Long id) {
        return service.getById(id);
    }

    @GetMapping
    public List<{{ module }}DTO> getAll() {
        return service.getAll();
    }

    @PutMapping("/{id}")
    public {{ module }}DTO update(@PathVariable Long id, @RequestBody {{ module }}DTO dto) {
        return service.update(id, dto);
    }

    @DeleteMapping("/{id}")
    public void delete(@PathVariable Long id) {
        service.delete(id);
    }
}
