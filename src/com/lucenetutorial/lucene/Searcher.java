package com.lucenetutorial.lucene;

import java.io.IOException;
import java.nio.file.Path;
import java.nio.file.Paths;

import org.apache.lucene.search.IndexSearcher;
import org.apache.lucene.search.PhraseQuery;
import org.apache.lucene.search.Query;
import org.apache.lucene.search.ScoreDoc;
import org.apache.lucene.search.TopDocs;
import org.apache.lucene.store.Directory;
import org.apache.lucene.store.FSDirectory;
import org.apache.lucene.analysis.standard.StandardAnalyzer;
import org.apache.lucene.document.Document;
import org.apache.lucene.index.CorruptIndexException;
import org.apache.lucene.index.DirectoryReader;
import org.apache.lucene.index.IndexReader;
import org.apache.lucene.queryparser.classic.MultiFieldQueryParser;
import org.apache.lucene.queryparser.classic.ParseException;
import org.apache.lucene.queryparser.classic.QueryParser;
public class Searcher {
   IndexSearcher indexSearcher;
   Directory indexDirectory;
   IndexReader indexReader;
   QueryParser queryParser;
   String[] fields = {LuceneConstants.PLACES, LuceneConstants.PEOPLE, LuceneConstants.TITLE, LuceneConstants.BODY};	//Stores the available fields
   MultiFieldQueryParser mfQueryParser;
   Query query;
   
	public Searcher(String indexDirectoryPath, String selectedConstant) throws IOException {	//Constructor
		Path indexPath = Paths.get(indexDirectoryPath);
		indexDirectory = FSDirectory.open(indexPath);
		indexReader = DirectoryReader.open(indexDirectory);
		indexSearcher = new IndexSearcher(indexReader);
		queryParser = new QueryParser(selectedConstant, new StandardAnalyzer());
		mfQueryParser = new MultiFieldQueryParser(fields, new StandardAnalyzer());
	}

	public TopDocs search(String searchQuery) throws IOException, ParseException {		//Makes single field searches
		queryParser.setDefaultOperator(QueryParser.Operator.OR);	//Set default search behavior
		query = queryParser.parse(searchQuery);
		System.out.println("query: "+ query.toString());
		return indexSearcher.search(query, LuceneConstants.MAX_SEARCH);
	}
   
	public TopDocs multiSearch(String[] searchQueries) throws IOException, ParseException {	//Searches through every field
		query = MultiFieldQueryParser.parse(searchQueries, fields, new StandardAnalyzer());
		System.out.println(query.toString());
		return indexSearcher.search(query, LuceneConstants.MAX_SEARCH);
	}

	public TopDocs andSearch(String searchQuery) throws IOException, ParseException {
		queryParser.setDefaultOperator(QueryParser.Operator.AND);
		query = queryParser.parse(searchQuery);
		System.out.println(query.toString());
		return indexSearcher.search(query, LuceneConstants.MAX_SEARCH);
	}
	
	public TopDocs phraseSearch(String searchquery, String selectedConstant) throws IOException, ParseException {
		queryParser.setDefaultOperator(QueryParser.Operator.OR);
		String[] splitQuery = searchquery.split(" ");
		PhraseQuery query = new PhraseQuery(1, selectedConstant, splitQuery);
		System.out.println(query.toString());
		return indexSearcher.search(query, LuceneConstants.MAX_SEARCH);
	}
	
	public Document getDocument(ScoreDoc scoreDoc) throws CorruptIndexException, IOException {
		return indexSearcher.doc(scoreDoc.doc);
	}

	public void close() throws IOException {
		indexReader.close();
		indexDirectory.close();
	}
}