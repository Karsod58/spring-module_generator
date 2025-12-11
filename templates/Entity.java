package {{ module|lower }}.entity;

import jakarta.persistence.*;
import lombok.*;

@Entity
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class {{ module }}Entity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    {% for f in fields.split(',') %}
    private String {{ f.split(':')[0] }};
    {% endfor %}
}
