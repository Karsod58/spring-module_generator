package {{ module|lower }}.mapper;

import {{ module|lower }}.dto.{{ module }}DTO;
import {{ module|lower }}.entity.{{ module }}Entity;

public class {{ module }}Mapper {

    public static {{ module }}DTO toDTO({{ module }}Entity entity) {
        if (entity == null) return null;
        return {{ module }}DTO.builder()
            {% for f in fields.split(',') %}
            .{{ f.split(':')[0] }}(entity.get{{ f.split(':')[0]|capitalize }}())
            {% endfor %}
            .build();
    }

    public static {{ module }}Entity toEntity({{ module }}DTO dto) {
        if (dto == null) return null;
        return {{ module }}Entity.builder()
            {% for f in fields.split(',') %}
            .{{ f.split(':')[0] }}(dto.get{{ f.split(':')[0]|capitalize }}())
            {% endfor %}
            .build();
    }
}
