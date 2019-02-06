package com.mypackage;

import org.apache.lucene.index.*;
import org.apache.lucene.search.DocIdSetIterator;
import org.apache.lucene.store.Directory;
import org.apache.lucene.store.FSDirectory;
import org.apache.lucene.util.BytesRef;
import org.apache.lucene.document.Document;

import java.io.*;
import java.nio.file.Paths;
import java.util.*;


public abstract class IndexFileReader {

    static String output= "";
    public static void main(String[] args) throws IOException {
        LinkedHashMap<String, MyLinkedList> invertedIndex = new LinkedHashMap<String, MyLinkedList>();
            //Creates the inverted index and stored the data in invertedIndex
        File jarDir = new File(ClassLoader.getSystemClassLoader().getResource(".").getPath());
        String indexPath = args[0];
        indexPath = jarDir+"/"+indexPath;
        invertedIndex = createInvertedIndex("index");
        //invertedIndex = createInvertedIndex(indexFile);
        String outputFile = args[1];
        outputFile =  jarDir+"/"+outputFile ;
        String inputFile= args[2];
        inputFile = jarDir+"/"+inputFile;

        //InputStreamReader is=new InputStreamReader(new FileInputStream("/home/vaishnavipakyala/IdeaProjects/IRProject2/input"),"UTF-8");
      //  InputStreamReader is=new InputStreamReader(new FileInputStream((inputFile),"UTF-8");
        BufferedReader bufferedReader= new BufferedReader(new FileReader(inputFile));
        String readline;
        Directory direct = FSDirectory.open(Paths.get(indexPath));
        DirectoryReader dir = DirectoryReader.open(direct);
        IndexReader indexReader = dir;
        List<Integer> docIds = new ArrayList<>();
        for(int i=0;i< indexReader.maxDoc(); i++){
            Document document = indexReader.document(i);
            docIds.add(Integer.parseInt(document.get("id")));
        }

        List<String> queries = new ArrayList<>();
        while((readline= bufferedReader.readLine()) !=null){
 
            queries.add(readline);
        }

        for (String query : queries){
            
        	TAATAND(invertedIndex, query);
            TAATOR(invertedIndex, query);
            DAATAndOp(invertedIndex,query,docIds);
            DAATOr(invertedIndex, query, docIds);
            

        }
        BufferedWriter writer = new BufferedWriter(new FileWriter(new File(outputFile), true));
        writer.append(output);
        writer.close();

    }

    public static LinkedHashMap<String, MyLinkedList> createInvertedIndex(String indexFilePath) throws IOException {
        int fieldsCount = 0, termsCount = 0, documentCount = 0, count = 0, docId =0;
        List<String> termList = new ArrayList<>();
       // LinkedList<Integer> linkedList;
        ArrayList<Object> mainlist = new ArrayList<>();
        LinkedHashMap<String, MyLinkedList> invertedIndex = new LinkedHashMap<String, MyLinkedList>();
        IndexReader indexReader;

        int size_corpus, num;
       // IndexReader indexReader;
        Directory direct = FSDirectory.open(Paths.get(indexFilePath));
      //  System.out.println("gg");
        //   LeafReaderContext segment = direct.leaves().get(0);
        DirectoryReader dir = DirectoryReader.open(direct);
        //LeafReaderContext segment = dir.leaves().get(0);
      //  System.out.println("ggg");
        indexReader = dir;
        size_corpus = indexReader.maxDoc();
//        System.out.println(size_corpus);
        Fields fields = MultiFields.getFields(indexReader);
        Terms terms;


        for (String field : fields) {

            if (!field.equals("id")) {

                //linkedList = new LinkedList<>();
                terms = MultiFields.getTerms(indexReader, field);
                if (terms == null) {
                    continue;
                }
                documentCount = indexReader.numDocs();
                TermsEnum termsEnum;
                termsEnum = terms.iterator();
                BytesRef scratch;
                PostingsEnum docsEnum;


                while ((scratch = termsEnum.next()) != null) {
                    MyLinkedList postings_new = new MyLinkedList();
                    termsCount++;
                   // linkedList = new LinkedList<>();
                    if (termsEnum.term().utf8ToString().equals("")) {
                        continue;
                    }
                    // System.out.println(scratch.utf8ToString());
                    termList.add(termsEnum.term().utf8ToString());
                    //count++;
                    docsEnum = termsEnum.postings(null, PostingsEnum.ALL);

                    while ((docId = docsEnum.nextDoc()) != DocIdSetIterator.NO_MORE_DOCS) {
                       postings_new.insert(docId);
                    }
               //     System.out.println("\n" + termsEnum.term().utf8ToString() + " DocFreq:" + termsEnum.docFreq() + " Postings size:" + postings_new.count);
               //    printlinkedList(postings_new);
                    invertedIndex.put(termsEnum.term().utf8ToString(), postings_new);
                }


            }
        }
  //      System.out.println("fields count=" + fieldsCount);
    //    System.out.println("Terms count=" + termsCount);
      //  System.out.println("Document count=" + documentCount);
        return invertedIndex;


    }
    public static void printlinkedList(MyLinkedList p1){
        Posting indexp1 =p1.head;
       System.out.print("Postings list:");
       output= output+"Postings list:";
        while(indexp1!= null) {
            System.out.print(" " + indexp1.docId);
            output=output+" " + indexp1.docId;
            indexp1 = indexp1.next;
        }
        System.out.println();
        output=output+"\n";

    }
    public static void printlinkedListResults(MyLinkedList p1){
        Posting indexp1 =p1.head;
        System.out.print("Results:");
        while(indexp1!= null) {
            System.out.print(" " + indexp1.docId);
            indexp1 = indexp1.next;
        }
    }
    public static void printArrayList(List<Integer> result, String query){
        String[] queryTerm = query.split(" ");
        System.out.println("DAATOr");
        output=output+"DAATOr"+"\n";
        for (String term : queryTerm) {
            System.out.print(term+" ");
            output=output+term+" ";
        }
        System.out.print("\n"+"Results:");
        output=output+"\n"+"Results:";
        for(int i=0;i<result.size();i++){
            System.out.print(" " +result.get(i));
            output=output+" " +result.get(i);
        }
        System.out.println();
        output=output+"\n";

    }
    public static void printArrayListResult(List<Integer> result){
        System.out.print("Results:");
        for(int i=0;i<result.size();i++){
            System.out.print(" " +result.get(i));

        }

    }

    public static MyLinkedList intersectPostings(MyLinkedList p1, MyLinkedList p2){
        MyLinkedList partial_list= new MyLinkedList();
        Posting indexp1 = p1.head;
        Posting indexp2 = p2.head;
        while(indexp1!=null && indexp2!=null){
            if(indexp1.docId == indexp2.docId){
                partial_list.insert(indexp1.docId);
                indexp1= indexp1.next;
                indexp2=indexp2.next;
            }
            else if(indexp1.docId<indexp2.docId){
                indexp1=indexp1.next;
            }
            else{
                indexp2=indexp2.next;
            }
        }
        return partial_list;
    }


    public static MyLinkedList intersectPostingsSkip(MyLinkedList p1, MyLinkedList p2){
        MyLinkedList partial_list= new MyLinkedList();
        Posting indexp1 = p1.head;
        Posting indexp2 = p2.head;
        int comparisioncount =0;
        while(indexp1!=null && indexp2!=null){

            if(indexp1.docId == indexp2.docId){
                partial_list.insert(indexp1.docId);
                indexp1= indexp1.next;
                indexp2=indexp2.next;
            }
            else if(indexp1.docId<indexp2.docId){
                Posting skippedNode = indexp1.skipTo;
                if (skippedNode != null) {
                    if (skippedNode.docId <= indexp2.docId)
                        indexp1 = indexp1.skipTo;
                    else
                        indexp1 = indexp1.next;
                } else
                    indexp1 = indexp1.next;
            }
            else if (indexp1.docId > indexp2.docId) {
                Posting skippedNode = indexp2.skipTo;
                if (skippedNode != null) {
                    if (skippedNode.docId < indexp1.docId)
                        indexp2 = indexp2.skipTo;
                    else
                        indexp2 = indexp2.next;
                } else
                    indexp2 = indexp2.next;
            }
            partial_list.comparisionCount++;
        }
        return partial_list;
    }

    public static MyLinkedList unionpostings(MyLinkedList p1, MyLinkedList p2) {

        MyLinkedList post= new MyLinkedList();
        Posting indexp1 = p1.head;
        Posting indexp2 = p2.head;
       // int comparisoncount =0;

        while(indexp1!=null && indexp2!=null) {
            if(indexp1.docId<indexp2.docId) {
                post.insert(indexp1.docId);
                indexp1=indexp1.next;
            }
            else if(indexp1.docId == indexp2.docId){
                post.insert(indexp1.docId);
                indexp1=indexp1.next;
                indexp2= indexp2.next;
            }
            else {
                post.insert(indexp2.docId);
                indexp2=indexp2.next;
            }
            post.comparisionCount++;
        }
        while(indexp1!=null) {
            post.insert(indexp1.docId);
            indexp1=indexp1.next;
            post.comparisionCount++;
        }
        while(indexp2!=null) {
            post.insert(indexp2.docId);
            indexp2=indexp2.next;
            post.comparisionCount++;
        }
        return post;
    }


    public static MyLinkedList TAATOR(LinkedHashMap<String, MyLinkedList> invertedIndex, String query) throws IOException {
        String[] queryTerms= query.split(" ");
        List<MyLinkedList> termPostings = new ArrayList<>();
        for (String term : queryTerms) {
            termPostings.add(invertedIndex.get(term));
        }

        //getSort(termPostings);
        Collections.sort(termPostings, new Comparator<MyLinkedList>() {
                    @Override
                    public int compare(MyLinkedList o1, MyLinkedList o2) {
                        return Integer.compare(o1.getcount(), o2.getcount());
                    }
                }
        );
        MyLinkedList partialPostings = termPostings.get(0);
        termPostings.remove(0);
        int compareCount =0;
        for (MyLinkedList currentPostings : termPostings) {
            partialPostings = unionpostings(partialPostings, currentPostings);
            compareCount= compareCount + partialPostings.comparisionCount;
        }
        OrResults(partialPostings, query);

        System.out.println("Number of documents in results: "+partialPostings.getcount());
        output=output+"Number of documents in results: "+partialPostings.getcount()+"\n";
        System.out.println("Number of comparisons: "+ compareCount);
        output=output+"Number of comparisons: "+ compareCount+"\n";
        return partialPostings;
    }
    public static void OrResults(MyLinkedList p1, String query){
        Posting indexp1 = p1.head;
        String[] queryTerms= query.split(" ");
        System.out.println("TAATOr");
        output=output+"TAATOr"+"\n";
      //  List<MyLinkedList> termPostings = new ArrayList<>();
        for (String term : queryTerms) {
            System.out.print(term+" ");
            output=output+term+" ";
        }
        System.out.println();
        output=output+"\n";
        System.out.print("Results:");
        output=output+"Results:";
        while(indexp1!= null) {
            System.out.print(" " + indexp1.docId);
            output=output+" " + indexp1.docId;
            indexp1 = indexp1.next;
        }
        System.out.println();
        output=output+"\n";

    }
    public static MyLinkedList TAATAND(LinkedHashMap<String, MyLinkedList> invertedIndex, String query) throws IOException {
        String[] queryTerms= query.split(" ");
        MyLinkedList partialPostings = new MyLinkedList();
        List<MyLinkedList> termPostings = new ArrayList<>();
        for (String term : queryTerms) {
            System.out.println("GetPostings" + "\n"+ term);
            output = output+"GetPostings" + "\n"+ term+"\n";
            termPostings.add(invertedIndex.get(term));
            printlinkedList(invertedIndex.get(term));
        }

        Collections.sort(termPostings, new Comparator<MyLinkedList>(){
                    @Override
                    public int compare(MyLinkedList o1, MyLinkedList o2) {
                        return Integer.compare(o1.getcount(), o2.getcount());
                    }
                }
        );
        int compareCount=0;
        partialPostings = termPostings.get(0);
        termPostings.remove(0);
        for (MyLinkedList currentPostings : termPostings) {
            partialPostings = intersectPostingsSkip(partialPostings, currentPostings);
            compareCount= compareCount + partialPostings.comparisionCount;
        }
        Andresults(partialPostings, query);

        System.out.println("Number of documents in results: "+partialPostings.getcount());
        output=output+"Number of documents in results: "+partialPostings.getcount()+"\n";
        System.out.println("Number of comparisons: "+ compareCount);
        output=output+"Number of comparisons: "+ compareCount+"\n";
        return partialPostings;
    }
    public static void Andresults(MyLinkedList p1, String query){
        Posting indexp1 = p1.head;
        String[] queryTerms= query.split(" ");
        System.out.println("TAATAnd");
        output=output+"TAATAnd"+"\n";
        for (String term : queryTerms){
            System.out.print(term+" ");
            output=output+term+" ";
        }
        System.out.println();
        output=output+"\n";
        System.out.print("Results:");
        output=output+"Results:";
        if(indexp1 ==null){
            System.out.println(" "+"empty");
            output=output+" "+"empty"+"\n";
        }
        else {
            while (indexp1 != null) {
                System.out.print(" " + indexp1.docId);
                output=output+" " + indexp1.docId;
                indexp1 = indexp1.next;
            }
            System.out.println();
            output=output+"\n";
        }
    }
    //System.out.println("-------------------");
    public static List<MyLinkedList> getSort(List<MyLinkedList> termPostings) {
        MyLinkedList sort1 = termPostings.get(0);
        MyLinkedList sort2 = termPostings.get(1);
        int sort1Size = sort1.getcount();
        int sort2Size = sort2.getcount();
        List<MyLinkedList> sortedPostings = new ArrayList<>();
        if(sort1Size < sort2Size){
            sortedPostings.add(sort1);
            sortedPostings.add(sort2);
        }
        else{
            sortedPostings.add(sort2);
            sortedPostings.add(sort1);
        }
        return sortedPostings;
    }


    public static List<Integer> DAATOr(LinkedHashMap<String, MyLinkedList> invertedIndex, String query, List<Integer> docIds) {

        String[] queryTerm = query.split(" ");
        List<Integer> result = new ArrayList<>();
        int count = 0,comparisonCount=0;
        boolean found = false;
        List<MyLinkedList> termPosting = new ArrayList<>();
        for (String term : queryTerm) {
            termPosting.add(invertedIndex.get(term));
        }
        for (Integer docId : docIds) {
            count = 0;
            for (MyLinkedList list : termPosting) {
                found = isFound(list,docId);
                if(found) {
                    result.add(docId);
                    break;
                }
            }
        }
        printArrayList(result,query);
        System.out.println("Number of documents in the results: "+ result.size());
        output=output+"Number of documents in the results: "+ result.size()+"\n";
        return result;
    }
    public static boolean isFound(MyLinkedList myLinkedList, int docId){
        boolean found = false;
        Posting current = myLinkedList.head;
        while (current != null) {
            if (docId == current.docId) {
                found = true;
                break;
            }
            else if (docId > current.docId)
            {
                current = current.next;
            }
            else if (docId < current.docId){
                break;
            }
        }
        return found;
    }

    public static List<Integer> DAATAnd(LinkedHashMap<String, MyLinkedList> invertedIndex, String query, List<Integer> docIds){
        String[] terms = query.split(" ");
        int comparisonCount =0;
        List<Integer> result = new ArrayList<>();
        int count =0;
        boolean found = false;
        List<MyLinkedList> termPosting = new ArrayList<>();
        List<Posting> curr= new ArrayList<>();
        int i=0;

        for (String term : terms) {
                termPosting.add(invertedIndex.get(term));
                Posting pointerArray= new Posting();
                pointerArray = termPosting.get(i).head;
                curr.add(pointerArray);
                i++;
        }

        int j=0;
        int counts=0;
        boolean flag= false;
        int docId=0, docId2=0;
        docId= curr.get(0).docId;
        Posting curr_j = new Posting();
        curr_j= curr.get(j);
        Posting curr_j1= new Posting();
        docId2= curr.get(1).docId;
        curr_j1= curr.get(j+1);
        while(j< curr.size()) {
            docId= curr_j.docId;

            while(curr.get(j+1).next !=null){
                docId2 =curr_j1.docId;
                if(docId == docId2){
                    counts++;
                    j++;
                    flag= true;
                    break;
                }
                else if(docId < docId2){
                    docId=curr.get(0).next.docId;
                    curr_j= curr.get(0).next;
                //    curr.get(0)= nextElement;
                    flag =false;
                    break;
                }
                else if(docId > docId2){
                    docId2 =  curr.get(j+1).next.docId;
                    curr_j1 =curr.get(j+1).next;
                    flag= false;
                }
            }
            if(flag== true){

                result.add(docId);
            }

        }

        /*

        for (Integer docId: docIds) {
            count =0;
            for (MyLinkedList list: termPosting) {
                found = isFoundAll(list,docId);
                if (found)
                    count++;
            }
            if(count == termPosting.size())
                result.add(docId);
        } */
        return result;
    }
    public static boolean isFoundAll(MyLinkedList sinkedList, int docId){
        boolean found = false;
        Posting current = sinkedList.head;
        while (current!=null){
            if(docId == current.docId) {
                found = true;
                break;
            }
            else if(docId > current.docId){
                Posting skippedNode = current.skipTo;
                if (skippedNode != null) {
                    if (docId > skippedNode.docId)
                        current= current.skipTo;
                    else
                        current = current.next;
                } else
                    current = current.next;
            }
            else if (docId < current.docId){
                break;
            }
        }
        return found;
    }
    
    public static List<Integer> DAATAndOp(LinkedHashMap<String, MyLinkedList> invertedIndex, String query, List<Integer> docIds){
        String[] terms = query.split(" ");
        int comparisonCounts =0;
        int counter=0;
        List<Integer> result = new ArrayList<>();
        int count =0;
        boolean found = false;
        List<MyLinkedList> termPosting = new ArrayList<>();
        for (String term : terms) {
            termPosting.add(invertedIndex.get(term));
        }
        for (Integer docId: docIds) {
            count =0;
            for (MyLinkedList list: termPosting) {
                found = isFoundAll(list,docId);
                if (found)
                    count++;
            }
            comparisonCounts++;
            counter= counter+comparisonCounts;
            if(count == termPosting.size())
                result.add(docId);
        }
        printArrayListAND(result,query);
        System.out.println("Number of documents in the results: "+ result.size());
        output=output+"Number of documents in the results: "+ result.size()+"\n";
        System.out.println("Number of comparisons: "+ counter);
        output=output+"Number of comparisons: "+ counter+"\n";
        return result;
    }
    
    public static void printArrayListAND(List<Integer> result, String query){
        String[] queryTerm = query.split(" ");
        System.out.println("DAATAnd");
        output=output+"DAATAnd"+"\n";
        for (String term : queryTerm) {
            System.out.print(term+" ");
            output=output+term+" ";
        }
        System.out.print("\n"+"Results:");
        output=output+"\n"+"Results:";
        for(int i=0;i<result.size();i++){
            System.out.print(" " +result.get(i));
            output=output+" " +result.get(i);
        }
        System.out.println();
        output=output+"\n";

    }
    
    
    }



