package com.mypackage;

public class MyLinkedList {
    int count;
    int comparisionCount=0;
   // int docId =0;

    public Posting head;
    public Posting tail;
   // MyLinkedList a= new MyLinkedList();
    MyLinkedList(){
        head= null;
        tail = null;
        count = 0;
        comparisionCount=0;

    }

    public void insert(int docId){
        Posting newPosting = new Posting(docId);
        if(head == null) {
            head = newPosting;
            tail = newPosting;

        }
        else {
            tail.next = newPosting;
           // tail = newPosting;
            tail= tail.next;
        }
        count++;
    }
    public int getcount(){
        return count;
    }

    public void updateSkip() {
        int skipSize = (int) Math.sqrt(count);
        int i = 0;
        Posting postingCounter = head;
        Posting skipPointer;
        while (postingCounter != null) {
            i = 0;
            skipPointer = postingCounter;
            while (skipPointer != null) {
                if (i == skipSize) {
                    postingCounter.skipTo = skipPointer;
                    break;
                }
                skipPointer = skipPointer.next;
                i++;
            }
            postingCounter = postingCounter.skipTo;
        }

    }
}
