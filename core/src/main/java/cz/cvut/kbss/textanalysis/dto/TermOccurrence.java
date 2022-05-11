package cz.cvut.kbss.textanalysis.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

import java.util.List;

/**
 * Represents analysis service output (when using css selector option)
 */
@Getter
@Setter
@AllArgsConstructor
public class TermOccurrence {
    private String about;
    private String property;
    private String resource;
    private String content;
    private String typeof;
    private double score;
    private int startOffset;
    private String originalTerm;
}