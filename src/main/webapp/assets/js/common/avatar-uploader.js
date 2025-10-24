/**
 * Avatar Uploader - Common Module
 * Xử lý upload avatar cho cả Customer và Shop
 */

class AvatarUploader {
  /**
   * @param {Object} config
   * @param {string} config.inputId - ID của input file
   * @param {string} config.previewId - ID của img preview
   * @param {string} config.uploadUrl - URL endpoint upload
   * @param {string} config.fileParamName - Tên param gửi lên server (vd: "avatarCustomer", "shopLogo")
   * @param {Function} config.onSuccess - Callback khi upload thành công
   * @param {Function} config.onError - Callback khi có lỗi
   * @param {string} config.action - Action name (vd: "uploadAvatar")
   * @param {string[]} config.additionalPreviewIds - Mảng các ID img preview khác (optional)
   */
  constructor(config) {
    this.input = document.getElementById(config.inputId);
    this.preview = document.getElementById(config.previewId);
    this.additionalPreviews = config.additionalPreviewIds
      ? config.additionalPreviewIds.map((id) => document.getElementById(id))
      : [];
    this.uploadUrl = config.uploadUrl;
    this.fileParamName = config.fileParamName;
    this.action = config.action || "uploadAvatar";
    this.onSuccess = config.onSuccess || this.defaultSuccessHandler;
    this.onError = config.onError || this.defaultErrorHandler;

    this.init();
  }

  init() {
    if (!this.input || !this.preview) {
      console.error("[AvatarUploader] Input or preview element not found");
      return;
    }

    this.input.addEventListener("change", () => this.handleFileSelect());
  }

  handleFileSelect() {
    const file = this.input.files[0];
    if (!file) return;

    // Validate client-side
    const validationError = this.validateFile(file);
    if (validationError) {
      this.showError(validationError);
      this.input.value = ""; // Clear input
      return;
    }

    // Preview ảnh trước khi upload
    this.previewImage(file);

    // Upload lên server
    this.uploadFile(file);
  }

  validateFile(file) {
    // Check file type
    if (!file.type.startsWith("image/")) {
      return "Vui lòng chọn file ảnh hợp lệ.";
    }

    // Check file size (5MB)
    if (file.size > 5 * 1024 * 1024) {
      return "Ảnh vượt quá dung lượng cho phép (5MB).";
    }

    return null;
  }

  previewImage(file) {
    const reader = new FileReader();
    reader.onload = (e) => {
      this.preview.src = e.target.result;

      // Update additional previews
      this.additionalPreviews.forEach((preview) => {
        if (preview) preview.src = e.target.result;
      });
    };
    reader.readAsDataURL(file);
  }

  uploadFile(file) {
    const formData = new FormData();
    formData.append("action", this.action);
    formData.append(this.fileParamName, file);

    fetch(this.uploadUrl, {
      method: "POST",
      body: formData,
    })
      .then((res) => res.json())
      .then((data) => {
        if (data.success) {
          this.onSuccess(data);
        } else {
          this.onError(data.message || "Upload thất bại.");
        }
      })
      .catch((error) => {
        console.error("[AvatarUploader] Upload error:", error);
        this.onError("Đã xảy ra lỗi khi upload ảnh.");
      });
  }

  showError(message) {
    if (typeof toast === "function") {
      toast({
        title: "Lỗi!",
        message: message,
        type: "error",
        duration: 3000,
      });
    } else {
      alert(message);
    }
  }

  defaultSuccessHandler(data) {
    if (typeof toast === "function") {
      toast({
        title: "Thành công!",
        message: data.message,
        type: "success",
        duration: 3000,
      });
    }

    // Update preview với URL từ server (nếu có)
    if (data.avatarUrl) {
      this.preview.src = data.avatarUrl;
      this.additionalPreviews.forEach((preview) => {
        if (preview) preview.src = data.avatarUrl;
      });
    }
  }

  defaultErrorHandler(message) {
    this.showError(message);
  }
}

// Export để sử dụng trong các file khác
window.AvatarUploader = AvatarUploader;
