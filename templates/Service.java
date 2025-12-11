package {{ module|lower }}.service;

import {{ module|lower }}.dto.{{ module }}DTO;

import java.util.List;

public interface {{ module }}Service {

    {{ module }}DTO create({{ module }}DTO dto);

    {{ module }}DTO getById(Long id);

    List<{{ module }}DTO> getAll();

    {{ module }}DTO update(Long id, {{ module }}DTO dto);

    void delete(Long id);
}
