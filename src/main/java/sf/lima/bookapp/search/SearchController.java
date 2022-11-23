package sf.lima.bookapp.search;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.util.StringUtils;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.reactive.function.client.ExchangeStrategies;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Mono;

import java.util.List;
import java.util.stream.Collectors;

@Controller
public class SearchController {
    private final String COVER_IMAGE_ROOT = "http://covers.openlibrary.org/b/id/";

    private final WebClient webClient;

    public SearchController(WebClient.Builder webClientBuilder ) {
        this.webClient = webClientBuilder.exchangeStrategies(ExchangeStrategies.builder()
                .codecs(clientCodecConfigurer -> clientCodecConfigurer
                        .defaultCodecs()
                        .maxInMemorySize(16*1024*1024))
                .build()).baseUrl("http://openlibrary.org/search.json").build();
    }

    @GetMapping("/search")
    public String getSearchResult(@RequestParam String query, Model model){
        Mono<SearchResult> resultMono = this.webClient.get()
                .uri("?q={querry}", query)
                .retrieve().bodyToMono(SearchResult.class);
        SearchResult searchResult = resultMono.block();
        List<SearchResultBook> books = searchResult.getDocs()
                .stream()
                .limit(10)
                .map(searchResultBook -> {
                    searchResultBook.setKey(searchResultBook.getKey().replace("/works/", ""));
                    String coverId = searchResultBook.getCover_i();
                    if (StringUtils.hasText(coverId)) {
                        coverId = COVER_IMAGE_ROOT + coverId + "-M.jpg";
                    } else coverId = "/images/no_image.png";
                    searchResultBook.setCover_i(coverId);
                    return searchResultBook;
                }).toList();
        model.addAttribute("searchResults",books);

        return "search";
    }
}
