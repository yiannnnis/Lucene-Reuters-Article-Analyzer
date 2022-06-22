package com.lucenetutorial.lucene;

import java.io.File;
import java.io.FileFilter;
import java.io.IOException;
import java.io.StringReader;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Scanner;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.apache.lucene.analysis.Analyzer;
import org.apache.lucene.analysis.CharArraySet;
import org.apache.lucene.analysis.TokenStream;
import org.apache.lucene.analysis.standard.StandardAnalyzer;
import org.apache.lucene.analysis.tokenattributes.CharTermAttribute;
import org.apache.lucene.document.Document;
import org.apache.lucene.document.Field;
import org.apache.lucene.document.StringField;
import org.apache.lucene.document.TextField;
import org.apache.lucene.index.CorruptIndexException;
import org.apache.lucene.index.IndexWriter;
import org.apache.lucene.index.IndexWriterConfig;
import org.apache.lucene.index.Term;
import org.apache.lucene.store.Directory;
import org.apache.lucene.store.FSDirectory;

public class Indexer {
	private IndexWriter writer;
	public Indexer(String indexDirectoryPath) throws IOException {
	//this directory will contain the indexes
	Path indexPath = Paths.get(indexDirectoryPath);
	if(!Files.exists(indexPath)) {
		Files.createDirectory(indexPath);
	}
	//Path indexPath = Files.createTempDirectory(indexDirectoryPath);
	Directory indexDirectory = FSDirectory.open(indexPath);
	//create the indexer
	IndexWriterConfig config = new IndexWriterConfig(new StandardAnalyzer());
	writer = new IndexWriter(indexDirectory, config);
	}
	
	public void close() throws CorruptIndexException, IOException {
		writer.close();
	}

	public static String removeStopWords(String textFile) {
		List<String> stopWords = Arrays.asList("a", "an", "and", "are", "as", "at", "be", "but", "by",
			   "for", "if", "in", "into", "is", "it",
			   "no", "not", "of", "on", "or", "such",
			   "that", "the", "their", "then", "there", "these",
			   "they", "this", "to", "was", "will", "with");
		CharArraySet stopSet = new CharArraySet(stopWords, true);
		try {
			ArrayList<String> remaining = new ArrayList<String>();
			Analyzer analyzer = new StandardAnalyzer(stopSet);
			TokenStream tokenStream = analyzer.tokenStream(LuceneConstants.BODY, new StringReader(textFile));
			CharTermAttribute term = tokenStream.addAttribute(CharTermAttribute.class);
			String finalString = new String();
			tokenStream.reset();
			while(tokenStream.incrementToken()) {
				remaining.add(term.toString());
			}
			tokenStream.close();
			analyzer.close();
			for(int j=0; j < remaining.size(); j++) {
				finalString += remaining.get(j) + " ";
			}
			System.out.println(finalString);
			return finalString;
		} catch (IOException e) {
			e.printStackTrace();
			return null;
		}
	}

	public Document getDocument(File file) throws IOException {
		Document document = new Document();
		//index file contents
		//BufferedReader br = new BufferedReader(new FileReader(file));
		PorterStemmer pStemmer = new PorterStemmer();
		Scanner fileScanner = new Scanner(file);
		String fileString = Files.readString(file.toPath());
		String temporary;
		String combine = new String();
		int i=0;
		//String temporary = br.readLine().toString();
		Pattern pla = Pattern.compile("<PLACES>(.*)</PLACES>", Pattern.DOTALL);
		Pattern pep = Pattern.compile("<PEOPLE>(.*)</PEOPLE>", Pattern.DOTALL);
		Pattern ttl = Pattern.compile("<TITLE>(.*)</TITLE>", Pattern.DOTALL);
		Pattern bdy = Pattern.compile("<BODY>(.*)</BODY>", Pattern.DOTALL);
		try {
			Matcher placesMatch = pla.matcher(fileString);
			placesMatch.find();
			temporary = placesMatch.group(1);
			Field placesField = new Field(LuceneConstants.PLACES, temporary, TextField.TYPE_STORED);
			document.add(placesField);
		} catch(IllegalStateException ise) {
		}
		try {
			Matcher peopleMatch = pep.matcher(fileString);
			peopleMatch.find();
			temporary = peopleMatch.group(1);
			Field peopleField = new Field(LuceneConstants.PEOPLE, temporary, TextField.TYPE_STORED);
			document.add(peopleField);
		} catch(IllegalStateException ise) {
		}
		try {
			Matcher titleMatch = ttl.matcher(fileString);
			titleMatch.find();
			temporary = titleMatch.group(1);
			for(char ch: temporary.toCharArray() ) {
				if(Character.isWhitespace(ch)) {
					pStemmer.stem();
					combine += pStemmer.toString() + " ";
					i++;
					continue;
				}
				pStemmer.add(ch);
				i++;
			}
			pStemmer.stem();
			combine += pStemmer.toString();
			Field titleField = new Field(LuceneConstants.TITLE, combine, TextField.TYPE_STORED);
			System.out.println(combine);
			document.add(titleField);
		} catch(IllegalStateException ise) {
		}
		try {
			Matcher bodyMatch = bdy.matcher(fileString);
			bodyMatch.find();
			temporary = removeStopWords(bodyMatch.group(1));
			for(char ch: temporary.toCharArray() ) {
				if(Character.isWhitespace(ch)) {
					pStemmer.stem();
					combine += pStemmer.toString() + " ";
					i++;
					continue;
				}
				pStemmer.add(ch);
				i++;
			}
			pStemmer.stem();
			combine += pStemmer.toString();
			Field bodyField = new Field(LuceneConstants.BODY, combine, TextField.TYPE_STORED);
			System.out.println(combine);
			document.add(bodyField);
		} catch(IllegalStateException ise) {
		}
		//index file name
		Field fileNameField = new Field(LuceneConstants.FILE_NAME, file.getName(), StringField.TYPE_STORED);
		//index file path
		Field filePathField = new Field(LuceneConstants.FILE_PATH, file.getCanonicalPath(), StringField.TYPE_STORED);
		document.add(fileNameField);
		document.add(filePathField);
		//br.close();
		fileScanner.close();
		return document;
	}

	public void deleteDocument(String filename) throws IOException{
		writer.deleteDocuments(new Term(LuceneConstants.FILE_NAME, filename));
		writer.commit();
	}

	private void indexFile(File file) throws IOException {
		System.out.println("Indexing "+file.getCanonicalPath());
		Document document = getDocument(file);
		writer.addDocument(document);
	}
	public int createIndex(String dataDirPath, FileFilter filter) throws IOException {
		//get all files in the data directory
		File[] files = new File(dataDirPath).listFiles();
		for (File file : files) {
			if(!file.isDirectory()
			&& !file.isHidden()
			&& file.exists()
			&& file.canRead()
            && filter.accept(file)
            ){
				indexFile(file);
			}
		}
		return writer.numRamDocs();
	}
}