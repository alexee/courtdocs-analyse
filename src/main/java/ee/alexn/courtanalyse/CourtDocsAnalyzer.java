package ee.alexn.courtanalyse;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashSet;
import java.util.List;
import java.util.Scanner;
import java.util.Set;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.core.io.ClassPathResource;
import org.springframework.stereotype.Service;

import ee.alexn.courtanalyse.service.StopWordService;
import ee.alexn.courtanalyse.service.WordSimilarityService;
import ee.alexn.courtanalyse.service.WordSimilarityService.Similarity;

@Service
public class CourtDocsAnalyzer implements CommandLineRunner {

	private static final Logger logger = LoggerFactory.getLogger(CourtDocsAnalyzer.class);
	
	@Autowired
	private WordSimilarityService similarityService;
	
	@Autowired
	private StopWordService stopWordService;
	
	public void run(String... arg0) throws Exception {
		ClassPathResource appealFailureFolder = new ClassPathResource("appeal_failure");
		File[] failedAppealFiles = appealFailureFolder.getFile().listFiles();
		for (File failedAppealFile : failedAppealFiles) {
			analyzeResource(failedAppealFile);
		}
	}

	private void analyzeResource(File file) {
		logger.info("Analyzing resource " + file);
		List<String> allWords = getFileWords(file);
		Set<String> words = new HashSet<String>(allWords);
		String baseWord = "negative";
		List<Similarity> similarities = new ArrayList<Similarity>();
		for (String word : words) {
			if (!word.equals(baseWord) && !stopWordService.isStopWord(word)) {
				similarities.add(similarityService.getWordSimilarity(baseWord, word));
			}
		}
		Collections.sort(similarities, new Comparator<Similarity>() {
			public int compare(Similarity o1, Similarity o2) {
				return -Double.compare(o1.similarity, o2.similarity);
			}
		});
		logger.info(similarities + "");
	}
	
	private List<String> getFileWords(File file) {
		List<String> ret = new ArrayList<String>();
		try {
			Scanner sc = new Scanner(file);
			while (sc.hasNext()) {
				String rawWord = sc.next();
				String word = cleanWord(rawWord);
				word = word.trim();
				if (!word.isEmpty()) {
					ret.add(word);
				}
			}
			sc.close();
		} catch (IOException e) {
			logger.error("Error reading resource " + file, e);
		}
		return ret;
	}

	private String cleanWord(String word) {
		word = word.toLowerCase();
		StringBuilder ret = new StringBuilder();
		for (Character c : word.toCharArray()) {
			if (isWordCharacter(c)) {
				ret.append(c);
			}
		}
		return ret.toString();
	}

	private boolean isWordCharacter(Character c) {
		return c >= 'a' && c <= 'z' || (c == '\'');
	}

}
