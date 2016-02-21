package ee.alexn.courtanalyse.service;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

@Service
public class WordSimilarityService {

	@Value("${similarity.service.endpoint}")
	public String similarityServiceURI;
	
	public Similarity getWordSimilarity(String word1, String word2) {
		RestTemplate restTemplate = new RestTemplate();
		String result = restTemplate.getForObject(similarityServiceURI + "?word1=" + word1 + "&word2=" + word2, String.class);
		return new Similarity(word1, word2, Double.parseDouble(result));
	}
	
	public class Similarity {
		public String word1;
		public String word2;
		public Double similarity;
	
		public Similarity(String w1, String w2, Double similarity) {
			this.word1 = w1;
			this.word2 = w2;
			this.similarity = similarity;
		}

		@Override
		public String toString() {
			return "Similarity [word1=" + word1 + ", word2=" + word2 + ", similarity=" + similarity + "]";
		}
		
	}
	
}
