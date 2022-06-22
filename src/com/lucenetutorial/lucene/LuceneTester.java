package com.lucenetutorial.lucene;

import java.io.IOException;
import org.apache.lucene.document.Document;
import org.apache.lucene.queryparser.classic.ParseException;
import org.apache.lucene.search.ScoreDoc;
import org.apache.lucene.search.TopDocs;
import java.util.Scanner;
import java.io.File;

public class LuceneTester {
	String indexDir = "C:\\Users\\John\\eclipse-workspace\\LuceneFirstApplication\\Index";
	String dataDir = "C:\\Users\\John\\eclipse-workspace\\LuceneFirstApplication\\Files";
	Indexer indexer;
	Searcher searcher;
		
	public static void main(String[] args) {
		LuceneTester tester; 
		File[] files = new File("C:\\Users\\John\\eclipse-workspace\\LuceneFirstApplication\\Files").listFiles();	//Opens every file within this directory
		boolean loopFlag = true;				//Is used to end the program
		Scanner input = new Scanner(System.in);	//Used for keyboard input
		do {
			System.out.println("Enter your choice:");		//Main menu
			System.out.println("1 - Import files to Index");
			System.out.println("2 - Remove articles");
			System.out.println("3 - Search query");
			System.out.println("0 - Exit application");
			String choice = input.nextLine();
			switch(choice) {
				case "1":
					System.out.println("Indexing files from directory");	//Using tester object to create or update Lucene index
    	  			try {
    	  				tester = new LuceneTester();
    	  				tester.createIndex();
    	  			}catch (IOException e) {
    	  				e.printStackTrace();
    	  			}
    	  		break;
				case "2":	//Deleting requested document
					try {
						tester = new LuceneTester();
						System.out.println("Insert filename");
						String requestedFile = input.nextLine();
						tester.deleteDocument(requestedFile);
					}catch(IOException e) {
						e.printStackTrace();
					}catch(ParseException e){
						e.printStackTrace();
					}
					
				break;
				case "3":		//Search menu
					System.out.println("1 - Search places");	//1-4 are single field searches
					System.out.println("2 - Search people");
					System.out.println("3 - Search title");
					System.out.println("4 - Search body");
					System.out.println("5 - Search on every field");	//5 searches every field with the same string
					System.out.println("6 - Search places (with AND)");
					System.out.println("7 - Search people (with AND)");
					System.out.println("8 - Search title (with AND)");
					System.out.println("9 - Search body (with AND)");
					System.out.println("10 - Search body (Phrase Search)");
					String searchChoice = input.nextLine();
					switch(searchChoice) {
						case "1":
							try {
								tester = new LuceneTester();
								System.out.println("Enter place search query");
								tester.search(input.nextLine(), LuceneConstants.PLACES);
							}catch(IOException e) {
								e.printStackTrace();
							}catch(ParseException e) {
								e.printStackTrace();
							}
						break;
						
						case "2":
							try {
								tester = new LuceneTester();
								System.out.println("Enter people search query");
								tester.search(input.nextLine(), LuceneConstants.PEOPLE);
							}catch(IOException e) {
								e.printStackTrace();
							}catch(ParseException e) {
								e.printStackTrace();
							}
						break;
						
						case "3":
							try {
								tester = new LuceneTester();
								System.out.println("Enter title search query");
								tester.search(input.nextLine(), LuceneConstants.TITLE);
							}catch(IOException e) {
								e.printStackTrace();
							}catch(ParseException e) {
								e.printStackTrace();
							}
						break;
						
						case "4":
							try {
								tester = new LuceneTester();
								System.out.println("Enter body search query");
								tester.search(input.nextLine(), LuceneConstants.BODY);
							}catch(IOException e) {
								e.printStackTrace();
							}catch(ParseException e) {
								e.printStackTrace();
							}
						break;
						
						case "5":
							try {
								tester = new LuceneTester();
								System.out.println("Enter search query");
								String[] query = new String[4];
								query[0] = query[1] = query[2] = query[3] = input.nextLine();
								tester.multiFieldSearch(query);
							}catch(IOException e) {
								e.printStackTrace();
							}catch(ParseException e) {
								e.printStackTrace();
							}
						break;
						
						case "6":
							try {
								tester = new LuceneTester();
								System.out.println("Enter place search query");
								tester.andSearch(input.nextLine(), LuceneConstants.PLACES);
							}catch(IOException e) {
								e.printStackTrace();
							}catch(ParseException e) {
								e.printStackTrace();
							}
						break;
						
						case "7":
							try {
								tester = new LuceneTester();
								System.out.println("Enter people search query");
								tester.andSearch(input.nextLine(), LuceneConstants.PEOPLE);
							}catch(IOException e) {
								e.printStackTrace();
							}catch(ParseException e) {
								e.printStackTrace();
							}
						break;
						
						case "8":
							try {
								tester = new LuceneTester();
								System.out.println("Enter title search query");
								tester.andSearch(input.nextLine(), LuceneConstants.TITLE);
							}catch(IOException e) {
								e.printStackTrace();
							}catch(ParseException e) {
								e.printStackTrace();
							}
						break;
						
						case "9":
							try {
								tester = new LuceneTester();
								System.out.println("Enter body search query");
								tester.andSearch(input.nextLine(), LuceneConstants.BODY);
							}catch(IOException e) {
								e.printStackTrace();
							}catch(ParseException e) {
								e.printStackTrace();
							}
						break;
						
						case "10":
							try {
								tester = new LuceneTester();
								System.out.println("Enter body search phrase");
								tester.phraseSearch(input.nextLine(), LuceneConstants.BODY);
							}catch(IOException e) {
								e.printStackTrace();
							}catch(ParseException e) {
								e.printStackTrace();
							}
						break;
					}
				break;
				
				case "0":
					loopFlag = false;
				break;
			}
    	  
		}while(loopFlag);
	}


	private void createIndex() throws IOException {		//Used to prepare an index
		indexer = new Indexer(indexDir);
		int numIndexed;
		long startTime = System.currentTimeMillis();
		numIndexed = indexer.createIndex(dataDir, new TextFileFilter());
		long endTime = System.currentTimeMillis();
		indexer.close();
		System.out.println(numIndexed+" File(s) indexed, time taken: " + (endTime-startTime)+" ms");	
   }
   
	private void search(String searchQuery, String selectedConstant) throws IOException, ParseException {	//Used for single field searches, selectedConstant decides which field is used
		searcher = new Searcher(indexDir, selectedConstant);
		long startTime = System.currentTimeMillis();
		TopDocs hits = searcher.search(searchQuery);
		long endTime = System.currentTimeMillis();
		System.out.println(hits.totalHits +" documents found. Time :" + (endTime - startTime));
		for(ScoreDoc scoreDoc : hits.scoreDocs) {
			Document doc = searcher.getDocument(scoreDoc);
			System.out.println("File: " + doc.get(LuceneConstants.FILE_PATH));
		}
		searcher.close();
	}

	private void multiFieldSearch(String[] searchQueries) throws IOException, ParseException{		//Used for searches including all fields
		searcher = new Searcher(indexDir, null);
		long startTime = System.currentTimeMillis();
		TopDocs hits = searcher.multiSearch(searchQueries);
		long endTime = System.currentTimeMillis();
		System.out.println(hits.totalHits +" documents found. Time :" + (endTime - startTime));
		for(ScoreDoc scoreDoc : hits.scoreDocs) {
			Document doc = searcher.getDocument(scoreDoc);
			System.out.println("File: " + doc.get(LuceneConstants.FILE_PATH));
		}
		searcher.close();
	}

	private void andSearch(String searchQuery, String selectedConstant) throws IOException, ParseException {	//Used for single field searches with Operator.AND
		searcher = new Searcher(indexDir, selectedConstant);
		long startTime = System.currentTimeMillis();
		TopDocs hits = searcher.andSearch(searchQuery);
		long endTime = System.currentTimeMillis();
		System.out.println(hits.totalHits +" documents found. Time:" + (endTime - startTime));
		for(ScoreDoc scoreDoc : hits.scoreDocs) {
			Document doc = searcher.getDocument(scoreDoc);
			System.out.println("File: " + doc.get(LuceneConstants.FILE_PATH));
		}
		searcher.close();
	}
	
	private void phraseSearch(String searchQuery, String selectedConstant) throws IOException, ParseException {
		searcher = new Searcher(indexDir, selectedConstant);
		long startTime = System.currentTimeMillis();
		TopDocs hits = searcher.phraseSearch(searchQuery, selectedConstant);
		long endTime = System.currentTimeMillis();
		System.out.println(hits.totalHits +" documents found. Time:" + (endTime - startTime));
		for(ScoreDoc scoreDoc : hits.scoreDocs) {
			Document doc = searcher.getDocument(scoreDoc);
			System.out.println("File: " + doc.get(LuceneConstants.FILE_PATH));
		}
		searcher.close();
	}

	private void deleteDocument(String filename) throws IOException, ParseException{		//Used when deleting a document
		Indexer indexer = new Indexer(indexDir);
		System.out.println("Deleting index for "+filename);
		indexer.deleteDocument(filename);
	}
}	