package com.vegetech.process;

import java.math.BigDecimal;
import java.util.List;

import org.json.JSONArray;
import org.json.JSONObject;

import com.vegetech.entity.Owner;
import com.vegetech.entity.Pet;

public class ProcessData {
	private DBProcessor dbProcessor = null;
	public String callDataBaseService(JSONObject reqJSON) {
		System.out.println("callDataBaseService Enter");
		String responseValue = "";
		try {
			if (reqJSON != null) {
				String action = (String) reqJSON.get("meta");
				if (action != null) {
					dbProcessor = new DBProcessor();
					System.out.println("dbProcessor:"+dbProcessor);
					System.out.println("action:"+action);
					ProcessData prD = new ProcessData();
					if (action.equals("createPet")) {
						responseValue = prD.convertCallCreatePet((JSONArray) reqJSON.get("body"));
					} else if (action.equals("getAllOwners")) {
						List<Owner> ownerDataList = dbProcessor.getOwners();
						responseValue = prD.convertListToJSON(ownerDataList);
					} else if (action.equals("getAllPets")) {
						List<Pet> petDataList = dbProcessor.getPets();
						responseValue = prD.convertListToJSON(petDataList);
					}
				}
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
		System.out.println("callDataBaseService Exit");
		return responseValue;
	}

	private String convertListToJSON(List<?> dbObjectList) {
		System.out.println("convertListToJSON enter");
		JSONObject mainObj = null;
		try {
			if (dbObjectList != null) {
				mainObj = new JSONObject();
				JSONObject jsnObj = null;
				JSONArray jsnAarr = null;
				if (dbObjectList.get(0) instanceof Owner) {
					jsnAarr = new JSONArray();
					mainObj.put("meta", "getAllOwners");
					List<Owner> ownerList = (List<Owner>) dbObjectList;
					Owner ownerObj = null;
					for (int i = 0; i < ownerList.size(); i++) {
						ownerObj = new Owner();
						ownerObj = ownerList.get(i);
						jsnObj = new JSONObject();
						jsnObj.put("firstname", ownerObj.getFirstName());
						jsnObj.put("lastname", ownerObj.getLastName());
						jsnObj.put("city", ownerObj.getCity());
						jsnObj.put("pet_id", ownerObj.getPet_id());
						jsnAarr.put(i, jsnObj);
					}
				} else if (dbObjectList.get(0) instanceof Pet) {
					jsnAarr = new JSONArray();
					mainObj.put("meta", "getAllPets");
					List<Pet> petList = (List<Pet>) dbObjectList;
					Pet petObj = null;
					for (int i = 0; i < petList.size(); i++) {
						petObj = new Pet();
						petObj = petList.get(i);
						jsnObj = new JSONObject();
						jsnObj.put("name", petObj.getName());
						jsnObj.put("birthday", petObj.getDateOfBirth());
						jsnObj.put("owner_id", petObj.getOwner_id());
						jsnAarr.put(i, jsnObj);
					}
				}
				mainObj.put("body", jsnAarr);
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
		System.out.println("convertListToJSON exit");
		if (mainObj != null) {
			System.out.println("mainObj:" + mainObj.toString());
			return mainObj.toString();
		} else {
			return null;
		}
	}

	private String convertCallCreatePet(JSONArray valuesArray) {
		System.out.println("convertCallCreatePet enter");
		String status = "success";
		try {
			if (valuesArray != null) {
				Pet petObj = null;
				for (int i = 0; i < valuesArray.length(); i++) {
					petObj = new Pet();
					JSONObject jsnObj = valuesArray.getJSONObject(i);
					System.out.println(jsnObj.getString("name"));
					System.out.println(jsnObj.getString("birthday"));
					System.out.println(jsnObj.getString("owner_name"));
					dbProcessor = new DBProcessor();
					System.out.println("dbProcessor:"+this.dbProcessor);
					petObj.setName(jsnObj.getString("name"));
					petObj.setDateOfBirth(jsnObj.getString("birthday"));
					BigDecimal ownerId = dbProcessor.getOwnerDetails(jsnObj.getString("owner_name"));
					petObj.setOwner_id(ownerId);
					BigDecimal petId = dbProcessor.generateNewPetId();
					petObj.setId(petId);
					dbProcessor.createNewPet(petObj);
				}
				status = "success";
			}
		} catch (Exception e) {
			e.printStackTrace();
			status = "failure";
		}
		System.out.println("convertCallCreatePet Exit");
		return status;
	}
}