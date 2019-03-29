package com.vegetech.servlet;

import java.io.PrintWriter;
import java.math.BigDecimal;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.json.JSONArray;
import org.json.JSONObject;

import com.vegetech.process.ProcessData;

public class RestUsingServlet extends HttpServlet {
	private Pattern ownerPattern = Pattern.compile("/owners");
	private Pattern petPattern = Pattern.compile("/pets");
	private Pattern ownerRcPattern = Pattern.compile("/owners/([0-9]*)");
	private Pattern petRcPattern = Pattern.compile("/pets/([0-9]*)");
	private Pattern createPetPattern = Pattern.compile("/createPet");
	private Matcher matcher;
	private ProcessData invoker = null;

	public void doGet(HttpServletRequest request, HttpServletResponse response) {
		System.out.println("in do get method");
		try {
			PrintWriter out = response.getWriter();
			String reqResourPath = request.getPathInfo();
			System.out.println("reqResourPath:" + reqResourPath);
			String respResource = getRcAndOper(reqResourPath, null, null, null);
			out.write(respResource);
		} catch (Exception e) {
			e.printStackTrace();
		}
		System.out.println("End of doGet");

	}

	public void doPost(HttpServletRequest request, HttpServletResponse response) {
		System.out.println("in do post method");
		try {
			PrintWriter out = response.getWriter();
			String reqResourPath = request.getPathInfo();
			System.out.println("reqResourPath:" + reqResourPath);
			String ownerName = request.getParameter("OwnerName");
			String petName = request.getParameter("PetName");
			String birthDate = request.getParameter("Birthdate");
			System.out.println("ownerName:"+ownerName);
			System.out.println("petName:"+petName);
			System.out.println("birthDate:"+birthDate);
			String respResource = getRcAndOper(reqResourPath, ownerName, petName, birthDate);
			out.write(respResource);
		} catch (Exception e) {
			e.printStackTrace();
		}
		System.out.println("End of doGet");
	}

	private String getRcAndOper(String pathInfo, String ownerName, String petName, String birthDate) {
		System.out.println("in getResource:" + pathInfo);
		String response = "No Data";
		try {
			BigDecimal id = null;
			invoker = new ProcessData();
			JSONObject reqJSON = new JSONObject();

			matcher = ownerRcPattern.matcher(pathInfo);
			if (matcher.find()) {
				id = new BigDecimal(matcher.group(1));
				System.out.println("Owner with resource request:" + id);
				// reqJSON.put("meta", "getOwnerWith"+id);
				return "Operation not supported";
			}
			matcher = petRcPattern.matcher(pathInfo);
			if (matcher.find()) {
				id = new BigDecimal(matcher.group(1));
				System.out.println("Pets with resource request:" + id);
				// reqJSON.put("meta", "getPetWith"+id);
				return "Operation not supported";
			}
			matcher = ownerPattern.matcher(pathInfo);
			if (matcher.find()) {
				System.out.println("Owner with resource request:" + matcher.group(0));
				reqJSON.put("meta", "getAllOwners");
			}
			matcher = petPattern.matcher(pathInfo);
			if (matcher.find()) {
				System.out.println("Pets with resource request:" + matcher.group(0));
				reqJSON.put("meta", "getAllPets");
			}
			matcher = createPetPattern.matcher(pathInfo);
			if (matcher.find()) {
				JSONArray reqArr = new JSONArray();
				JSONObject reqObjData = new JSONObject();
				if (petName != null) {
					reqObjData.put("name", petName);
				}
				if (birthDate != null) {
					reqObjData.put("birthday", birthDate);
				}
				if (ownerName != null) {
					reqObjData.put("owner_name", ownerName);
				}
				reqArr.put(0, reqObjData);
				reqJSON.put("body", reqArr);
				System.out.println("Create new Pet request" + matcher.group(0));
				reqJSON.put("meta", "createPet");
			}
			System.out.println("Calling db");
			response = invoker.callDataBaseService(reqJSON);
		} catch (Exception e) {
			e.printStackTrace();
		}
		return response;
	}
}