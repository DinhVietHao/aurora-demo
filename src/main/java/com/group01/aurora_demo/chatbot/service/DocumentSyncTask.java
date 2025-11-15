package com.group01.aurora_demo.chatbot.service;

import com.group01.aurora_demo.chatbot.dao.AuroraDataIngestDAO;

public class DocumentSyncTask implements Runnable {
    @Override
    public void run() {
        try {
            System.out.println("[SYNC] Starting document sync job...");
            AuroraDataIngestDAO dao = new AuroraDataIngestDAO();
            dao.syncAllDocuments();
            System.out.println("[SYNC] Completed document sync job.");
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
