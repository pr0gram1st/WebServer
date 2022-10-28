package org.example;

public class myThread extends Thread{
    public int Id;
    public myThread(int id){
        this.Id = id;
    }
    @Override
    public void run(){
        System.out.println("This is the thread #" + this.Id);
    }
}
