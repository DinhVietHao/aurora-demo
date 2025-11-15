package com.group01.aurora_demo.chatbot.controller;

import org.json.*;

import com.group01.aurora_demo.chatbot.dao.AuroraDataIngestDAO;
import com.group01.aurora_demo.chatbot.model.Document;
import java.net.URI;
import java.net.http.*;
import jakarta.servlet.http.HttpServlet;
import java.nio.charset.StandardCharsets;
import java.util.List;

import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.annotation.MultipartConfig;

@MultipartConfig
@WebServlet("/api/chat")
public class ChatbotServlet extends HttpServlet {

    private String apiKey;
    private AuroraDataIngestDAO dataIngestDAO;

    @Override
    public void init() {
        this.dataIngestDAO = new AuroraDataIngestDAO();
        this.apiKey = getServletContext().getInitParameter("GOOGLE_AI_API_KEY");
    }

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response) {
        try {
            request.setCharacterEncoding("UTF-8");
            response.setContentType("application/json; charset=UTF-8");

            String userMessage = request.getParameter("message");

            List<Document> relevantDocs = dataIngestDAO.searchRelevantDocuments(userMessage, 5);
            StringBuilder context = new StringBuilder();
            for (Document d : relevantDocs) {
                context.append(d.getTitle()).append(": ").append(d.getContent()).append("\n");
            }

            String prompt = String.format(
                    """
                                Bạn là AuroraBot - Trợ lý AI chuyên nghiệp và thân thiện của website bán sách trực tuyến - Aurora.
                                Thông tin ngữ cảnh từ cơ sở dữ liệu:
                                %s

                                Quy tắc khi đối thoại với khách hàng:
                                1. CHỈ SỬ DỤNG thông tin từ phần THÔNG TIN NGỮ CẢNH ở trên
                                2. Nếu KHÔNG CÓ thông tin chính xác, hãy trả lời: "Hiện tôi chưa có thông tin chính xác về vấn đề này. Bạn có thể tìm kiếm trực tiếp trên website hoặc liên hệ hỗ trợ viên."
                                3. KHÔNG ĐƯỢC BỊA RA thông tin không có trong ngữ cảnh
                                4. Đối với câu hỏi về SÁCH, cung cấp đầy đủ: tên, giá, tác giả, thể loại, đánh giá trung bình, đã bán được bao nhiêu quyển
                                5. Đối với VOUCHER: mã, điều kiện áp dụng, thời hạn, shop áp dụng
                                6. Giữ câu trả lời TỰ NHIÊN, THÂN THIỆN nhưng CHUYÊN NGHIỆP
                                7. Ưu tiên thông tin MỚI NHẤT và CÓ ĐỘ LIÊN QUAN CAO

                                Câu hỏi của người dùng: %s
                                TRẢ LỜI (ngắn gọn, hữu ích, dựa trên ngữ cảnh, không bịa thông tin)
                            """,
                    context.toString(), userMessage);

            String reply = callGeminiAPI(prompt);

            JSONObject json = new JSONObject();
            json.put("reply", reply);
            response.getWriter().print(json.toString());
        } catch (Exception e) {
            System.out.println("[ERROR] doPost of ChatbotServlet: " + e.getMessage());
        }
    }

    private String callGeminiAPI(String userPrompt) {
        try {
            HttpClient client = HttpClient.newHttpClient();
            String endpoint = String.format(
                    "https://generativelanguage.googleapis.com/v1beta/models/gemini-2.0-flash:generateContent?key=%s",
                    apiKey);

            // Tạo JSON body
            JSONObject content = new JSONObject()
                    .put("role", "user")
                    .put("parts", new JSONArray()
                            .put(new JSONObject().put("text", userPrompt)));

            JSONObject requestBody = new JSONObject()
                    .put("contents", new JSONArray().put(content));

            // Tạo request
            HttpRequest request = HttpRequest.newBuilder()
                    .uri(URI.create(endpoint))
                    .header("Content-Type", "application/json")
                    .POST(HttpRequest.BodyPublishers.ofString(requestBody.toString(), StandardCharsets.UTF_8))
                    .build();

            HttpResponse<String> response = client.send(request,
                    HttpResponse.BodyHandlers.ofString(StandardCharsets.UTF_8));

            return parseGeminiResponse(response.body());
        } catch (Exception e) {
            return "Aurora đang gặp sự cố: " + e.getMessage();
        }
    }

    private String parseGeminiResponse(String body) {
        try {
            JSONObject json = new JSONObject(body);
            JSONArray candidates = json.optJSONArray("candidates");
            if (candidates != null && !candidates.isEmpty()) {
                JSONObject first = candidates.getJSONObject(0);
                JSONObject content = first.getJSONObject("content");
                JSONArray parts = content.optJSONArray("parts");
                if (parts != null && !parts.isEmpty()) {
                    return parts.getJSONObject(0).optString("text", "Xin lỗi, tôi chưa có câu trả lời.");
                }
            }
        } catch (Exception e) {
            System.out.println("[ERROR] parseGeminiResponse in ChatbotServlet: " + e.getMessage());
        }
        return "Xin lỗi, tôi chưa có câu trả lời.";
    }

    public static void main(String[] args) {
        try {
            ChatbotServlet chatbot = new ChatbotServlet();
            String testPrompt = """
                    Bạn là AuroraBot - trợ lý AI thử nghiệm.
                    Câu hỏi của người dùng: Giới thiệu ngắn gọn về Aurora bookstore.
                    """;

            System.out.println("🔹 Đang gửi yêu cầu đến Gemini API...");
            String result = chatbot.callGeminiAPI(testPrompt);
            System.out.println("Phản hồi từ API:\n" + result);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

}