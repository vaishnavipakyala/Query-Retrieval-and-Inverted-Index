package com.mypackage;

public class Posting {

        int docId=0;
        //int sizeofSKip=0;
        Posting skipTo;

        Posting next;
    Posting(){
        next=null;
        skipTo=null;

    }


        Posting(int docId){
            this.docId=docId;
            next=null;
            skipTo=null;
        }

    }
