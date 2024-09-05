package openchat.easytalk.GenericDTOs;

import lombok.Data;

import java.util.List;

@Data
public class ListDTO<G> {
    private List<G> Values;
}
