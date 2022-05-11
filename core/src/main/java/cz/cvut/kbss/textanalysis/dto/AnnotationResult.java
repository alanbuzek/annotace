package cz.cvut.kbss.textanalysis.dto;

import lombok.AllArgsConstructor;
import lombok.Data;

import java.util.List;

@Data
@AllArgsConstructor
public class AnnotationResult {
    private String annotatedDocument;
    private List<TermOccurrencesSelector> termOccurrencesSelectors;
}