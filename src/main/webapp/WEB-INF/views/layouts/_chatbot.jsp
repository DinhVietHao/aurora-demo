<%@ page contentType="text/html; charset=UTF-8" pageEncoding="UTF-8" %>
    <%-- Widget Chatbot: Hiện nổi góc phải, phong cách hiện đại, phù hợp Aurora --%>

        <link rel="stylesheet" href="${ctx}/assets/css/common/chatbot.css?v=1.0.1" />

        <!-- Floating Button -->
        <button id="chatbot-btn" title="Chat với Aurora">
            <i class="bi bi-robot"></i>
        </button>

        <div id="chatbot-box">
            <div id="chatbot-header">
                <span class="aurora-bot-avatar"><i class="bi bi-robot"></i></span>
                Aurora - Trợ lý AI
                <button id="chatbot-close" title="Đóng"><i class="bi bi-x-lg"></i></button>
            </div>
            <div id="chatbot-messages"></div>
            <form id="chatbot-form" autocomplete="off">
                <input type="text" id="chatbot-input" placeholder="Nhập câu hỏi..." autocomplete="off" required />
                <button type="submit" id="chatbot-send"><i class="bi bi-send"></i></button>
            </form>
        </div>

        <script>
            (function () {
                const btn = document.getElementById('chatbot-btn');
                const box = document.getElementById('chatbot-box');
                const closeBtn = document.getElementById('chatbot-close');
                const messages = document.getElementById('chatbot-messages');
                const form = document.getElementById('chatbot-form');
                const input = document.getElementById('chatbot-input');

                // Open/close chatbot
                btn.onclick = () => {
                    box.style.display = 'flex';
                    btn.style.display = 'none';
                    input.focus();
                    setTimeout(() => scrollToBottom(), 120);
                    if (messages.childElementCount === 0) addBotMessage("Chào bạn! Tôi là Aurora - trợ lý AI. Bạn cần hỗ trợ gì về sách hoặc website?");
                };

                closeBtn.onclick = () => {
                    box.style.display = 'none';
                    btn.style.display = 'flex';
                };

                // Submit question
                form.onsubmit = async (e) => {
                    e.preventDefault();
                    const msg = input.value.trim();
                    if (!msg) return;
                    const formData = new FormData();
                    formData.append("message", msg);
                    addUserMessage(msg);
                    input.value = '';
                    addBotMessage("...", true); // loading
                    scrollToBottom();

                    try {
                        const res = await fetch("/api/chat", {
                            method: 'POST',
                            body: formData
                        });
                        const data = await res.json();

                        removeLoading();
                        addBotMessage(data.reply || "Xin lỗi, tôi chưa có câu trả lời.");
                        scrollToBottom();
                    } catch (err) {
                        console.error("Chat fetch error:", err);
                        removeLoading();
                        addBotMessage("Xin lỗi, hệ thống đang bận. Vui lòng thử lại sau.");
                        scrollToBottom();
                    }
                };

                function addUserMessage(text) {
                    const row = document.createElement('div');
                    row.className = 'message-row message-user';
                    row.innerHTML = '<div class="bubble">' + escapeHTML(text) + '</div>';
                    messages.appendChild(row);
                }

                function addBotMessage(text, loading) {
                    const row = document.createElement('div');
                    row.className = 'message-row message-bot';
                    const cls = 'bubble' + (loading ? ' loading' : '');
                    row.innerHTML = '<div class="' + cls + '">' + escapeHTML(text) + '</div>';
                    messages.appendChild(row);
                }

                function removeLoading() {
                    const last = messages.querySelector('.bubble.loading');
                    if (last) last.parentElement.remove();
                }

                function scrollToBottom() {
                    messages.scrollTop = messages.scrollHeight;
                }

                function escapeHTML(str) {
                    if (!str && str !== 0) return '';
                    return String(str).replace(/[&<>"']/g, function (m) {
                        return ({
                            '&': '&amp;', '<': '&lt;', '>': '&gt;',
                            '"': '&quot;', "'": '&#39;'
                        })[m];
                    });
                }
            })();
        </script>