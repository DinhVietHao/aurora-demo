package com.group01.aurora_demo.profile.controller;

import java.io.IOException;
import java.io.PrintWriter;
import java.util.List;

import org.json.JSONObject;

import com.group01.aurora_demo.auth.model.User;
import com.group01.aurora_demo.catalog.controller.NotificationServlet;
import com.group01.aurora_demo.profile.dao.AddressDAO;
import com.group01.aurora_demo.profile.model.Address;

import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;

@WebServlet("/address/*")
public class AddressServlet extends NotificationServlet {
    private AddressDAO addressDAO;

    public AddressServlet() {
        this.addressDAO = new AddressDAO();
    }

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp)
            throws ServletException, IOException {
        resp.setContentType("application/json;charset=UTF-8");
        PrintWriter out = resp.getWriter();
        JSONObject json = new JSONObject();

        HttpSession session = req.getSession();
        User user = (User) session.getAttribute("AUTH_USER");

        if (user == null) {
            resp.sendRedirect(req.getContextPath() + "/home");
            return;
        }

        req.setAttribute("user", user);
        String path = req.getPathInfo();
        if (path == null || path.equals("/")) {
            List<Address> addresses = this.addressDAO.getAddressesByUserId(user.getId());
            req.setAttribute("addresses", addresses);
            req.getRequestDispatcher("/WEB-INF/views/customer/address/address.jsp").forward(req, resp);
        } else if (path.equals("/update")) {
            try {
                Long addressIdEdit = Long.parseLong(req.getParameter("addressId"));
                Address address = this.addressDAO.getAddressById(user.getId(), addressIdEdit);
                json.put("addressId", address.getAddressId());
                json.put("recipientName", address.getRecipientName());
                json.put("phone", address.getPhone());
                json.put("province", address.getCity());
                json.put("provinceId", address.getProvinceId());
                json.put("district", address.getDistrict());
                json.put("districtId", address.getDistrictId());
                json.put("ward", address.getWard());
                json.put("wardCode", address.getWardCode());
                json.put("description", address.getDescription());
                json.put("defaultAddress", address.getUserAddress().isDefaultAddress());
                out.print(json.toString());
            } catch (Exception e) {
                System.out.println(e.getMessage());
            }
        }

    }

    @Override
    protected void doPost(HttpServletRequest req, HttpServletResponse resp)
            throws IOException {
        resp.setContentType("application/json;charset=UTF-8");
        PrintWriter out = resp.getWriter();
        JSONObject json = new JSONObject();

        String path = req.getPathInfo();
        HttpSession session = req.getSession();
        User user = (User) session.getAttribute("AUTH_USER");

        if (user == null) {
            resp.sendRedirect(req.getContextPath() + "/home");
            return;
        }

        req.setAttribute("user", user);
        switch (path) {
            case "/update":
                try {
                    long addressIdUpdate = Long.parseLong(req.getParameter("addressId"));
                    String from = req.getParameter("from");

                    String recipientName = req.getParameter("fullName");
                    String phone = req.getParameter("phone");
                    String city = req.getParameter("cityName");
                    String district = req.getParameter("districtName");
                    String ward = req.getParameter("wardName");
                    String description = req.getParameter("description");
                    int provinceId = Integer.parseInt(req.getParameter("provinceId"));
                    int districtId = Integer.parseInt(req.getParameter("districtId"));
                    String wardCode = req.getParameter("wardCode");
                    boolean isDefault = req.getParameter("isDefault") != null;

                    Address address = new Address();
                    address.setAddressId(addressIdUpdate);
                    address.setRecipientName(recipientName);
                    address.setPhone(phone);
                    address.setCity(city);
                    address.setProvinceId(provinceId);
                    address.setDistrict(district);
                    address.setDistrictId(districtId);
                    address.setWard(ward);
                    address.setWardCode(wardCode);
                    address.setDescription(description);
                    this.addressDAO.updateAddress(user.getId(), address, isDefault);

                    session.setAttribute("toastType", "success");
                    session.setAttribute("toastMsg", "Cập nhật địa chỉ thành công.");

                    if (from.equalsIgnoreCase("checkout")) {
                        resp.sendRedirect(req.getContextPath() + "/checkout?addressId=" + addressIdUpdate);
                    } else {
                        resp.sendRedirect(req.getContextPath() + "/address");
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                    System.out.println(e.getMessage());
                    session.setAttribute("toastType", "error");
                    session.setAttribute("toastMsg", "Có lỗi xảy ra khi cập nhật địa chỉ.");
                    resp.sendRedirect(req.getContextPath() + "/address");
                }

                break;
            case "/add":
                String recipientName = req.getParameter("fullName");
                String phone = req.getParameter("phone");
                String city = req.getParameter("cityName");
                String district = req.getParameter("districtName");
                String ward = req.getParameter("wardName");
                String description = req.getParameter("description");
                int provinceId = Integer.parseInt(req.getParameter("provinceId"));
                int districtId = Integer.parseInt(req.getParameter("districtId"));
                String wardCode = req.getParameter("wardCode");
                boolean isDefault = req.getParameter("isDefault") != null;

                Address address = new Address();
                address.setRecipientName(recipientName);
                address.setPhone(phone);
                address.setCity(city);
                address.setProvinceId(provinceId);
                address.setDistrict(district);
                address.setDistrictId(districtId);
                address.setWard(ward);
                address.setWardCode(wardCode);
                address.setDescription(description);
                this.addressDAO.addAddress(user.getId(), address, isDefault);

                String from = req.getParameter("from");
                if (from.equalsIgnoreCase("checkout")) {
                    resp.sendRedirect(req.getContextPath() + "/checkout");
                } else {
                    resp.sendRedirect(req.getContextPath() + "/address");
                }
                break;
            case "/delete":
                try {
                    Long addressIdDelete = Long.parseLong(req.getParameter("addressId"));

                    addressDAO.deleteAddress(user.getId(), addressIdDelete);
                    json.put("success", true);

                } catch (Exception e) {
                    System.out.println(e.getMessage());
                    json.put("success", false);
                    json.put("message", e.getMessage());
                }
                out.print(json.toString());
                break;
            case "/set-default":
                try {
                    long addressId = Long.parseLong(req.getParameter("addressId"));
                    this.addressDAO.setDefaultAddress(user.getId(), addressId);
                    resp.sendRedirect(req.getContextPath() + "/address");
                } catch (Exception e) {
                    System.out.println(e.getMessage());
                }
                break;
            default:
                resp.sendRedirect(req.getContextPath() + "/address");
        }

    }

}