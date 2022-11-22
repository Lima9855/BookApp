package sf.lima.bookapp;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import sf.lima.bookapp.author.Author;
import sf.lima.bookapp.author.AuthorRepository;
import sf.lima.bookapp.book.Book;
import sf.lima.bookapp.book.BookRepository;

import javax.annotation.PostConstruct;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.Stream;



/**
 * used to load starter data to cassandra
 *
 */
//@Component
public class DataLoader {
    @Autowired
    AuthorRepository authorRepository;
    @Autowired
    BookRepository bookRepository;

    @Value("${datadump.location.author}")
    private String authorDumpLocation;

    @Value("${datadump.location.works}")
    private String worksDumpLocation;

    private void initAuthors() {
        Path path = Paths.get(authorDumpLocation);
        try (Stream<String> lines = Files.lines(path)) {
            lines.forEach(line -> {
                try {
                    String jsonString = line.substring(line.indexOf("{"));
                    JSONObject jsonObject = new JSONObject(jsonString);
                    Author author = new Author();
                    author.setName(jsonObject.optString("name"));
                    author.setPersonalName(jsonObject.optString("personal_name"));
                    author.setId(jsonObject.optString("key").replace("/authors/", ""));
                    authorRepository.save(author);
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            });

        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private void initWorks() {
        Path path = Paths.get(worksDumpLocation);
        System.out.println("HELLO FROM INIT WORKS");
        DateTimeFormatter dateTimeFormatter = DateTimeFormatter.ofPattern("yyyy-MM-dd'T'HH:mm:ss.SSSSSS");
        try (Stream<String> lines = Files.lines(path)) {
            lines.forEach(line -> {
                try {
                    String jsonString = line.substring(line.indexOf("{"));
                    JSONObject jsonObject = new JSONObject(jsonString);
                    Book book = new Book();
                    book.setId(jsonObject.optString("key").replace("/works/", ""));
                    book.setName(jsonObject.optString("title"));
                    JSONObject jsonDescription = jsonObject.optJSONObject("description");
                    if (jsonDescription != null) {
                        book.setDescription(jsonDescription.optString("value"));
                    }
                    JSONObject jsonCreated = jsonObject.optJSONObject("created");
                    book.setPublishedDate(LocalDate.parse(jsonCreated.optString("value"), dateTimeFormatter));
                    JSONArray coversArray = jsonObject.optJSONArray("covers");
                    if (coversArray != null) {
                        List<String> coverIds = new ArrayList<>();
                        for (int i = 0; i < coversArray.length(); i++) {
                            coverIds.add(String.valueOf(coversArray.get(i)));
                        }
                        book.setCoverIds(coverIds);
                    }
                    JSONArray jsonAuthorArr = jsonObject.optJSONArray("author");
                    if (jsonAuthorArr != null) {
                        List<String> authorList = new ArrayList<>();
                        for (int i = 0; i < jsonAuthorArr.length(); i++) {
                            String authorId = jsonAuthorArr.getJSONObject(i)
                                    .getJSONObject("author")
                                    .getString("key")
                                    .replace("/author/", "");
                            authorList.add(authorId);
                        }
                        book.setAuthorIds(authorList);
                        List<String> authorNames = authorList.stream().map(id -> authorRepository.findById(id))
                                .map(optionalAuthor -> {
                                    if (optionalAuthor.isEmpty()) return "Unknown Author";
                                    return optionalAuthor.get().getName();
                                }).toList();
                        book.setAuthorNames(authorNames);

                    }
                    System.out.println("BOOK " + book.getId() + " is saved");
                    bookRepository.save(book);

                } catch (Exception e) {
                    e.printStackTrace();
                }
            });

        } catch (IOException e) {
            e.printStackTrace();
        }

    }


    public void start() {
        //initAuthors();
        //initWorks();
        System.out.println("działam");
    }
}
