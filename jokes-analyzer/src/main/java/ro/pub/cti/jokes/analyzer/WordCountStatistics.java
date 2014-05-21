package ro.pub.cti.jokes.analyzer;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;
import java.util.TreeMap;
import ro.pub.cti.jokes.db.Category;
import ro.pub.cti.jokes.db.DbAccess;
import ro.pub.cti.jokes.db.Joke;
import ro.pub.cti.jokes.db.JokesHibernateUtil;
import ro.pub.cti.jokes.db.Subcategory;

/**
 *
 * @author vlad
 */
public class WordCountStatistics {

    private Map<String, Integer> _wordCount;
    private TreeMap<String, Integer> _sortedWords;
    private int _toatalWordsCount;
    private DbAccess _dbAccess;
    private Map<Joke, Integer> _jokes;
    private Map<Joke, Integer> _jokesComments;
    private Map<Joke, Float> _jokesRatings;
    private Map<Joke, Integer> _jokesVotes;
    private TreeMap<Joke, Integer> _topJokes;
    private TreeMap<Joke, Integer> _topJokesComments;
    private TreeMap<Joke, Float> _topJokesRatings;
    private TreeMap<Joke, Integer> _topJokesVotes;
    List<Category> categories;
    int subCategoriesCount;
    int commentsCount;
    int votesCount;
    float totalRating;

    public WordCountStatistics(DbAccess dbAccess) {
        _wordCount = new HashMap<>();
        _jokesComments = new HashMap<>();
        _jokesRatings = new HashMap<>();
        _jokesVotes = new HashMap<>();
        _toatalWordsCount = 0;
        _dbAccess = dbAccess;
    }

    public void countAllCorpusWords() {
        _jokes = getJokes();
        System.out.println("Total jokes to process " + _jokes.size());
        _jokes.entrySet().stream().forEach(this::countWords);
        showStatistics();
        countCategories();
        System.out.println("Total joke count " + _jokes.size());
        
        System.out.println("Jokes with most categories");
        ReverseJokeComparator rvl = new ReverseJokeComparator(_jokes);
        _topJokes = new TreeMap<>(rvl);
        _topJokes.putAll(_jokes);
        _topJokes.entrySet().stream().limit(100).forEach((item) -> {
            System.out.println(item.getValue() + " " + item.getKey().getTitle());
        });
        
        System.out.println("Total comments " + commentsCount);
        System.out.println("Total votes " + votesCount);
        System.out.println("Average rating " + totalRating / _jokes.size());
        
        System.out.println("Jokes with most comments");
        ReverseJokeComparator rvl1 = new ReverseJokeComparator(_jokesComments);
        _topJokesComments = new TreeMap<>(rvl1);
        _topJokesComments.putAll(_jokesComments);
        _topJokesComments.entrySet().stream().limit(100).forEach((item) -> {
            System.out.println(item.getValue() + " " + item.getKey().getTitle());
        });
        
        System.out.println("Jokes with highest rating");
        ReverseJokeFloatComparator rvl2 = new ReverseJokeFloatComparator(_jokesRatings);
        _topJokesRatings = new TreeMap<>(rvl2);
        _topJokesRatings.putAll(_jokesRatings);
        _topJokesRatings.entrySet().stream().limit(100).forEach((item) -> {
            System.out.println(item.getValue() + " " + item.getKey().getTitle());
        });
        
        
        System.out.println("Jokes with most votes");
        ReverseJokeComparator rvl3 = new ReverseJokeComparator(_jokesVotes);
        _topJokesVotes = new TreeMap<>(rvl3);
        _topJokesVotes.putAll(_jokesVotes);
        _topJokesVotes.entrySet().stream().limit(100).forEach((item) -> {
            System.out.println(item.getValue() + " " + item.getKey().getTitle());
        });


    }

    public void countCategories() {
        subCategoriesCount = 0;
        System.out.println("Categories count " + categories.size());
        for (Category category : categories) {
            Set<Subcategory> subcategories = category.getSubcategories();
            subCategoriesCount += subcategories.size();
        }
        System.out.println("Total subcategories " + subCategoriesCount);
        System.out.println("Avg # of subcategories / categ: " + (float)subCategoriesCount / categories.size());
    }

    private Map<Joke, Integer> getJokes() {
        categories = _dbAccess.listCategorieses();
        commentsCount = 0;
        votesCount = 0;
        Map<Joke, Integer> jokesMap = new HashMap<>();
        for (Category category : categories) {
            System.out.println(category.getName());
            Set<Subcategory> subcategories = category.getSubcategories();
            for (Subcategory subcat : subcategories) {
                System.out.println("\t" + subcat.getName());
                List<Joke> jokes = _dbAccess.getJokes(subcat);
                jokes.stream().forEach((joke) -> {
                    if (null == jokesMap.get(joke)) {
                        jokesMap.put(joke, 1);
                        _jokesComments.put(joke, joke.getCommentsCount());
                        _jokesRatings.put(joke, joke.getRating());
                        _jokesVotes.put(joke, joke.getVotes());
                        commentsCount += joke.getCommentsCount();
                        votesCount += joke.getVotes();
                        totalRating += joke.getRating();

                    } else {
                        jokesMap.put(joke, jokesMap.get(joke) + 1);
                    }
                });
            }
        }
        return jokesMap;
    }

    private void countWords(Entry<Joke, Integer> item) {
        Joke joke = item.getKey();
        String text = joke.getContent();
        if (text.equals("")) {
            System.out.println("No content");
            return;
        }
        System.out.println(text);

        String words[] = text.split("\\s+");
        System.out.println("Total words count " + words.length);
        _toatalWordsCount += words.length;
        for (String word : words) {
            word = word.toLowerCase();
            if (isWord(word)) {
                if (_wordCount.get(word) != null) {
                    _wordCount.put(word, _wordCount.get(word) + 1);
                } else {
                    _wordCount.put(word, 1);
                }
            }
        }
    }

    public void showStatistics() {
        int vocabularySize = _wordCount.size();
        System.out.println("Vocabulary size: " + vocabularySize);
        System.out.println("Total words count: " + _toatalWordsCount);
        ReverseValueComparator rvl = new ReverseValueComparator(_wordCount);
        _sortedWords = new TreeMap<>(rvl);
        _sortedWords.putAll(_wordCount);

        int limit = 100;
        int count = 0;
        for (String key : _sortedWords.navigableKeySet()) {
            System.out.println(_wordCount.get(key) + " " + key);
            count++;
            if (count == limit) {
                break;
            }
        }
    }

    private boolean isWord(String word) {
        return word.matches("[a-z'\\?!\\(\\)\\[\\]\\{\\}]+");
    }

    public static void main(String[] args) {
        DbAccess dbTests = new DbAccess();
//        List<Category> categories = dbTests.listCategorieses();
//        for (Category category : categories){
//            System.out.println(category.getName());
//            Set<Subcategory> subcategories = category.getSubcategories();
//            for(Subcategory subcat: subcategories){
//                System.out.println("\t" + subcat.getName());
//                List<Joke> jokes = dbTests.getJokes(subcat);
//                for(Joke joke : jokes){
//                    System.out.println("\t\t" + joke.getTitle());
//                }
//            }
//        }

        WordCountStatistics stats = new WordCountStatistics(dbTests);
        stats.countAllCorpusWords();

        JokesHibernateUtil.getSessionFactory().close();
    }
}
