package cz.cvut.kbss.textanalysis.model;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;

@JsonIgnoreProperties(ignoreUnknown = true)
public class KerResult {

    private List<String> keywords;

    public KerResult() {
    }

    public List<String> getKeywords() {
        return keywords;
    }

    public void setKeywords(List<String> keywords) {
        this.keywords = keywords;
    }

    public static KerResult createEmpty() {
        KerResult response = new KerResult();
        response.setKeywords(Collections.emptyList());
        return response;
    }

    public static KerResult createSomeList() {
        List<String> keywords = Arrays.asList("park", "město", "metropolitní", "technický");
        KerResult response = new KerResult();
        response.setKeywords(keywords);
        return response;
    }

    @Override
    public String toString() {
        return "KerResult{" +
                "keywords=" + keywords +
                '}';
    }
}
