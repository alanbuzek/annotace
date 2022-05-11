package cz.cvut.kbss.textanalysis.dto;

import lombok.Getter;
import lombok.Setter;

import java.util.ArrayList;
import java.util.List;

@Getter
@Setter
public class TermOccurrencesSelector {
    private List<String> cssSelectors = new ArrayList<>();
    private List<TermOccurrence> termOccurrences = new ArrayList<>();
}
