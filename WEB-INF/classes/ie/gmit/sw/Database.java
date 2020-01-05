package ie.gmit.sw;

import java.util.Map;

public interface Database {
    public void add(CharSequence charSequence, Language language);
    public void resize(int max);
    public Map<Integer, LanguageEntry> getTopOccurrence(int max, Language language);
    public Language getLanguage(Map<Integer, LanguageEntry> query);
}//End interface
