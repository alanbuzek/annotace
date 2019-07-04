/**
 * Annotac
 * Copyright (C) 2019 Czech Technical University in Prague
 *
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program.  If not, see <https://www.gnu.org/licenses/>.
 * © 2019 GitHub, Inc.
 */
package cz.cvut.kbss.textanalysis.service;

import cz.cvut.kbss.textanalysis.lemmatizer.model.LemmatizerResult;
import cz.cvut.kbss.textanalysis.model.Phrase;
import cz.cvut.kbss.textanalysis.model.Word;
import cz.cvut.kbss.textanalysis.lemmatizer.LemmatizerApi;
import cz.cvut.kbss.textanalysis.Stopwords;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import cz.cvut.kbss.textanalysis.keywordextractor.model.KeywordExtractorResult;
import cz.cvut.kbss.textanalysis.lemmatizer.model.SingleLemmaResult;
import cz.cvut.kbss.textanalysis.model.QueryResult;

@Service
public class AnnotationService {

    @Autowired
    private LemmatizerApi morphoDitaService;

    public Stopwords stopwords = new Stopwords();

    List<String> stopwordsList;

    public List<Word> getAnnotations(final String textChunk, final List<QueryResult> queryResultList, final KeywordExtractorResult result, String lang) throws AnnotationException {
        try {
            return this._getAnnotations(textChunk, queryResultList, result, lang);
        } catch (Exception e) {
            throw new AnnotationException("Annotation failed.", e);
        }
    }

    private List<Word> _getAnnotations(final String textChunk, final List<QueryResult> queryResultList,final KeywordExtractorResult result, String lang) {
        final LemmatizerResult lemmatizerResult = morphoDitaService.process(textChunk, lang);
        return annotateOntologieLables(lemmatizerResult, queryResultList,result, lang);
    }

    private List<Word> annotateOntologieLables(LemmatizerResult lemmatizerResult, List<QueryResult> queryResultList, final KeywordExtractorResult kerResult, String lang) {

        List<Word> annotationsResults = new ArrayList<>();

        stopwordsList = stopwords.getStopwords(lang);

        for (List<SingleLemmaResult> results : lemmatizerResult.getResult()) {
            for (SingleLemmaResult result : results) {

                List<Phrase> matchedAnnotations = new ArrayList<>();
                boolean isKeyword = kerResult.getKeywords().contains(result.getLemma());
                boolean isStopword = stopwordsList.contains(result.getLemma());
                boolean isMatched = false;
                boolean isNotNegation;

                for (QueryResult queryResults : queryResultList) {
                    for (SingleLemmaResult ontologyResults : queryResults.getMorphoDitaResultList()) {

                        isMatched = queryResults.getMorphoDitaResultList().size() == 1;
                        isNotNegation = result.isNegated() ^ ontologyResults.isNegated();

                        if ((result.getLemma().contentEquals(ontologyResults.getLemma())) &&
                             isNotNegation) {

                            Phrase matchedAnnotation = new Phrase(
                                    queryResults.getType(),
                                    (kerResult.getKeywords().contains(result.getLemma())
                                                    &&(result.getToken().equals(queryResults.getLabel()))),
                                    isMatched,
                                    queryResults.getLabel());

                                    matchedAnnotations.add(matchedAnnotation);
                                }
                    }
                }

                if((matchedAnnotations.isEmpty()) && (isKeyword) && !(isStopword) && !(isMatched)) {
                    Phrase matchedAnnotation = new Phrase(
                                            "",
                                            true,
                                            true,
                            "");
                                    matchedAnnotations.add(matchedAnnotation);
                }

                final SingleLemmaResult res = result;
                annotationsResults.add( new Word(res.getLemma(), res.getToken(), res.getSpaces() == null ? "":res.getSpaces(), matchedAnnotations.toArray(new Phrase[]{})) );

            }
        }

        return annotationsResults;
    }

    private boolean checkForNegation(String s, String r) {
//        checkForNegation(result.getTag(), ontologyResults.getTag());
        if (s.length() > 10)
            return (s.charAt(10) == r.charAt(10));
        else
            return true;
    }

}
