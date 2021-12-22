package com.example.dry.Activity;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.net.Socket;

public class ChatClient {

        private Socket socket;
        private BufferedWriter bufferedWriter;
        private BufferedReader bufferedReader;
        private String senderName;

        public ChatClient(Socket socket, String senderName){
            try {
                this.socket = socket;
                this.bufferedReader = new BufferedReader(new InputStreamReader(socket.getInputStream()));
                this.bufferedWriter = new BufferedWriter(new OutputStreamWriter(socket.getOutputStream()));
                this.senderName = senderName;
            }catch (IOException e ){
                closeEverything(socket, bufferedReader, bufferedWriter);
            }
        }


        public void closeEverything(Socket socket, BufferedReader bufferedReader, BufferedWriter bufferedWriter){
            try{
                if(bufferedReader != null){
                    bufferedReader.close();
                }
                if(bufferedWriter != null){
                    bufferedWriter.close();
                }
                if(socket != null){
                    socket.close();
                }
            }catch (IOException e ){
                e.printStackTrace();
            }
        }

    }
