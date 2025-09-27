// AJAX Utilities for OneShop
class AjaxUtils {
  constructor() {
    this.baseUrl = "/api";
    this.csrfToken = this.getCsrfToken();
  }

  getCsrfToken() {
    const token = document.querySelector('meta[name="_csrf"]');
    return token ? token.getAttribute("content") : "";
  }

  async request(url, options = {}) {
    const defaultOptions = {
      headers: {
        "Content-Type": "application/json",
        "X-Requested-With": "XMLHttpRequest",
      },
    };

    if (this.csrfToken) {
      defaultOptions.headers["X-CSRF-TOKEN"] = this.csrfToken;
    }

    const finalOptions = { ...defaultOptions, ...options };

    try {
      const response = await fetch(url, finalOptions);
      const data = await response.json();

      if (!response.ok) {
        throw new Error(data.message || "Request failed");
      }

      return data;
    } catch (error) {
      console.error("AJAX Error:", error);
      throw error;
    }
  }

  async get(url, params = {}) {
    const queryString = new URLSearchParams(params).toString();
    const fullUrl = queryString ? `${url}?${queryString}` : url;
    return this.request(fullUrl, { method: "GET" });
  }

  async post(url, data = {}) {
    return this.request(url, {
      method: "POST",
      body: JSON.stringify(data),
    });
  }

  async put(url, data = {}) {
    return this.request(url, {
      method: "PUT",
      body: JSON.stringify(data),
    });
  }

  async delete(url) {
    return this.request(url, { method: "DELETE" });
  }

  async postFormData(url, formData) {
    const options = {
      method: "POST",
      body: formData,
      headers: {
        "X-Requested-With": "XMLHttpRequest",
      },
    };

    if (this.csrfToken) {
      options.headers["X-CSRF-TOKEN"] = this.csrfToken;
    }

    try {
      const response = await fetch(url, options);
      const data = await response.json();

      if (!response.ok) {
        throw new Error(data.message || "Request failed");
      }

      return data;
    } catch (error) {
      console.error("AJAX Error:", error);
      throw error;
    }
  }

  showNotification(message, type = "success") {
    const alertClass =
      type === "success"
        ? "alert-success"
        : type === "error"
        ? "alert-danger"
        : type === "warning"
        ? "alert-warning"
        : "alert-info";

    const notification = document.createElement("div");
    notification.className = `alert ${alertClass} alert-dismissible fade show position-fixed`;
    notification.style.cssText =
      "top: 20px; right: 20px; z-index: 9999; min-width: 300px;";
    notification.innerHTML = `
            ${message}
            <button type="button" class="btn-close" data-bs-dismiss="alert"></button>
        `;

    document.body.appendChild(notification);

    // Auto remove after 5 seconds
    setTimeout(() => {
      if (notification.parentNode) {
        notification.parentNode.removeChild(notification);
      }
    }, 5000);
  }

  showLoading(element) {
    if (element) {
      element.disabled = true;
      const originalText = element.innerHTML;
      element.innerHTML =
        '<span class="spinner-border spinner-border-sm me-2"></span>Đang xử lý...';
      element.setAttribute("data-original-text", originalText);
    }
  }

  hideLoading(element) {
    if (element && element.hasAttribute("data-original-text")) {
      element.disabled = false;
      element.innerHTML = element.getAttribute("data-original-text");
      element.removeAttribute("data-original-text");
    }
  }
}

// Global instance
const ajax = new AjaxUtils();

// Category AJAX functions
class CategoryAjax {
  static async getAll() {
    return ajax.get("/api/category");
  }

  static async getById(id) {
    return ajax.post("/api/category/getCategory", { id });
  }

  static async add(categoryName, icon = null) {
    const formData = new FormData();
    formData.append("categoryName", categoryName);
    if (icon) {
      formData.append("icon", icon);
    }
    return ajax.postFormData("/api/category/addCategory", formData);
  }

  static async update(categoryId, categoryName, icon = null) {
    const formData = new FormData();
    formData.append("categoryId", categoryId);
    formData.append("categoryName", categoryName);
    if (icon) {
      formData.append("icon", icon);
    }
    return ajax.postFormData("/api/category/updateCategory", formData);
  }

  static async delete(categoryId) {
    return ajax.post("/api/category/deleteCategory", { categoryId });
  }
}

// Product AJAX functions
class ProductAjax {
  static async getAll(params = {}) {
    return ajax.get("/api/product", params);
  }

  static async getById(id) {
    return ajax.post("/api/product/getProduct", { id });
  }

  static async add(productData, image = null) {
    const formData = new FormData();
    Object.keys(productData).forEach((key) => {
      formData.append(key, productData[key]);
    });
    if (image) {
      formData.append("image", image);
    }
    return ajax.postFormData("/api/product/addProduct", formData);
  }

  static async update(productId, productData, image = null) {
    const formData = new FormData();
    formData.append("productId", productId);
    Object.keys(productData).forEach((key) => {
      formData.append(key, productData[key]);
    });
    if (image) {
      formData.append("image", image);
    }
    return ajax.postFormData("/api/product/updateProduct", formData);
  }

  static async delete(productId) {
    return ajax.post("/api/product/deleteProduct", { productId });
  }

  static async updateStock(productId, quantity) {
    return ajax.post("/api/product/updateStock", { productId, quantity });
  }

  static async changeStatus(productId, status) {
    return ajax.post("/api/product/changeStatus", { productId, status });
  }
}

// Table management utilities
class TableManager {
  static createProductRow(product) {
    const statusClass =
      product.status === "ACTIVE"
        ? "bg-success"
        : product.status === "INACTIVE"
        ? "bg-warning"
        : "bg-danger";
    const stockClass =
      product.stockQuantity > 10
        ? "bg-success"
        : product.stockQuantity > 0
        ? "bg-warning"
        : "bg-danger";

    return `
            <tr data-product-id="${product.productId}">
                <td><span class="badge bg-light text-dark">${
                  product.productId
                }</span></td>
                <td>
                    <div class="d-flex align-items-center">
                        ${
                          product.productImage
                            ? `<img src="${product.productImage}" alt="${product.productName}" 
                                  class="img-thumbnail" style="width: 60px; height: 60px; object-fit: cover; border-radius: 8px;">`
                            : `<div class="bg-light d-flex align-items-center justify-content-center" 
                                  style="width: 60px; height: 60px; border-radius: 8px;">
                                <i class="fas fa-image text-muted"></i>
                             </div>`
                        }
                    </div>
                </td>
                <td>
                    <div>
                        <h6 class="mb-1">${product.productName}</h6>
                        <small class="text-muted">${
                          product.description || ""
                        }</small>
                    </div>
                </td>
                <td><span class="badge bg-info">${
                  product.category.categoryName
                }</span></td>
                <td><strong class="text-success">${new Intl.NumberFormat(
                  "vi-VN"
                ).format(product.price)} ₫</strong></td>
                <td><span class="badge ${stockClass}">${
      product.stockQuantity
    }</span></td>
                <td><span class="badge ${statusClass}">${
      product.status
    }</span></td>
                <td>
                    <div class="btn-group" role="group">
                        <button class="btn btn-warning btn-sm" onclick="editProduct(${
                          product.productId
                        })" title="Chỉnh sửa">
                            <i class="fas fa-edit"></i>
                        </button>
                        <button class="btn btn-info btn-sm" onclick="viewProduct(${
                          product.productId
                        })" title="Xem chi tiết">
                            <i class="fas fa-eye"></i>
                        </button>
                        <button class="btn btn-danger btn-sm" onclick="deleteProduct(${
                          product.productId
                        })" title="Xóa">
                            <i class="fas fa-trash"></i>
                        </button>
                    </div>
                </td>
            </tr>
        `;
  }

  static createCategoryRow(category) {
    return `
            <tr data-category-id="${category.categoryId}">
                <td><span class="badge bg-light text-dark">${
                  category.categoryId
                }</span></td>
                <td>
                    <div class="d-flex align-items-center">
                        <div class="me-3">
                            <i class="fas fa-folder text-primary"></i>
                        </div>
                        <div>
                            <h6 class="mb-0">${category.categoryName}</h6>
                            <small class="text-muted">Danh mục sản phẩm</small>
                        </div>
                    </div>
                </td>
                <td>
                    ${
                      category.categoryImage
                        ? `<img src="${category.categoryImage}" alt="${category.categoryName}" 
                              class="img-thumbnail" style="width: 60px; height: 60px; object-fit: cover; border-radius: 8px;">`
                        : `<div class="bg-light d-flex align-items-center justify-content-center" 
                              style="width: 60px; height: 60px; border-radius: 8px;">
                            <i class="fas fa-image text-muted"></i>
                         </div>`
                    }
                </td>
                <td><span class="badge bg-success">Hoạt động</span></td>
                <td>
                    <div class="btn-group" role="group">
                        <button class="btn btn-warning btn-sm" onclick="editCategory(${
                          category.categoryId
                        })" title="Chỉnh sửa">
                            <i class="fas fa-edit"></i>
                        </button>
                        <button class="btn btn-danger btn-sm" onclick="deleteCategory(${
                          category.categoryId
                        })" title="Xóa">
                            <i class="fas fa-trash"></i>
                        </button>
                    </div>
                </td>
            </tr>
        `;
  }

  static updateTable(tableBodyId, data, createRowFunction) {
    const tbody = document.getElementById(tableBodyId);
    if (!tbody) return;

    tbody.innerHTML = "";
    if (data && data.length > 0) {
      data.forEach((item) => {
        tbody.insertAdjacentHTML("beforeend", createRowFunction(item));
      });
    } else {
      tbody.innerHTML =
        '<tr><td colspan="100%" class="text-center py-5">Không có dữ liệu</td></tr>';
    }
  }
}

// Global functions for backward compatibility
window.ajax = ajax;
window.CategoryAjax = CategoryAjax;
window.ProductAjax = ProductAjax;
window.TableManager = TableManager;
