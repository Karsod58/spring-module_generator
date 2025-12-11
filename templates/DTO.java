package {{ module|lower }}.dto;

import lombok.*;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class {{ module }}DTO {

    {% for f in fields.split(',') %}
    private String {{ f.split(':')[0] }};
    {% endfor %}
}
