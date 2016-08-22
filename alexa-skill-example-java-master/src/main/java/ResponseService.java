import org.json.JSONObject;

public class ResponseService {
	// Get request type from JSON request
	public static String getRequestType(JSONObject obj){
		
		JSONObject requestTypeObject = obj.getJSONObject("request");
		String requestType =  requestTypeObject.getString("type");
		
		return requestType;
	}
	
	// Get slot value for the IntentRequest
	public static String getSlotValue(JSONObject obj,String slotObjectName){
		
		JSONObject requestTypeObject = obj.getJSONObject("request");
		JSONObject intentObject = requestTypeObject.getJSONObject("intent");
		JSONObject slotsObject = intentObject.getJSONObject("slots");
		
		String name = slotsObject.getJSONObject(slotObjectName).getString("value");
	
		return name;
	}
	
	// Construct the JSON response sent back to the skill service
	public static JSONObject constructResponseObject(JSONObject obj, String requestType){
		
                JSONObject outputSpeech = new JSONObject();
		
                outputSpeech.put("shouldEndSession", false);
		String outputSpeechText;
		String outputSpeechType;
                
                String previousIntent = "";
				
		if (requestType.equals("LaunchRequest")){
			
			outputSpeechText = "ask me the closest branch or Atm!";
			outputSpeechType = "PlainText";
			
		}
		else if (requestType.equals("IntentRequest")){
			
			JSONObject requestTypeObject = obj.getJSONObject("request");
                        JSONObject intentObject = requestTypeObject.getJSONObject("intent");
                        String intentName = intentObject.getString("name");
                        
                        outputSpeechText = "";
                        
                        if(intentName.equals("GetAtm")){
                            previousIntent = "GetAtm";
                            outputSpeechText = "The closest ATM is 100m from you";
                        }
                        else if(intentName.equals("GetBranch")){
                            previousIntent = "GetBranch";
                            outputSpeechText = "The closest branch is 500m from your location.";
                        }
                        else if(intentName.equals("GetLocationInstructions")){
                            
                            JSONObject sessionObject = obj.getJSONObject("session");
                            JSONObject attributesObject = sessionObject.getJSONObject("attributes");
                            
                            String previousAttribute = attributesObject.getString("previousIntent");
                            
                            if(previousAttribute.equals("GetAtm")){
                                outputSpeechText = "You have to walk 100m to get there. It is next to the Pick and Pay.";
                                previousIntent = "GetAtm";
                            }
                            else if(previousAttribute.equals("GetBranch")){
                                outputSpeechText = "Turn right at the robot and walk another 300 meters.";
                                previousIntent = "GetBranch";
                            }
           
                        }
                       else if(intentName.equals("GetServices")){
                           
                           
                           JSONObject sessionObject = obj.getJSONObject("session");
                            JSONObject attributesObject = sessionObject.getJSONObject("attributes");
                            
                            String previousAttribute = attributesObject.getString("previousIntent");
                           
                        if(previousAttribute.equals("GetBranch")){
                            outputSpeechText = "The services they offer are as follows, home loans, private banking and prestige banking.";
                            previousIntent = "GetServices";
                        }
                        else if(previousAttribute.equals("GetAtm")){
                                outputSpeechText = "The services they offer are bank statement print out and cash deposit ";
                                previousIntent = "GetServices";
                            }
                     
                       }else if("GetInstruction".equals(intentName)){
                       
                         String slot= getSlotValue(obj,"AtmServices");
                         
                         if(slot.contains("deposit")){
                             
                                 outputSpeechText = "Okay cool! kindly press the deposit button, enter all the required information, then insert the amount of money you want to deposit";
                                previousIntent = "GetInstruction";
                             
                         }else if(slot.contains("statement")){
                                outputSpeechText = "Okay cool! kindly insert your bank card on the card slot, enter your pin, and then select bank statement, followed by period of the bank statement you need";
                                previousIntent = "GetInstruction";
                         }
                       
                       }
                       else if("AtmService".equals(intentName)){
                       
                         String slot= getSlotValue(obj,"Atm");
                        
                          previousIntent = "GetAtm";
                         if(slot.contains("statement")){
                             
                                outputSpeechText = "your nearest atm where you can print out statement is at rosebank mall";
                                
                             
                         }else if(slot.contains("deposit")){
                                outputSpeechText = "your nearest atm where you can deposit money is at sandton street";
                                
                         }
                         
                       
                       }
                       else if("BranchServices".equals(intentName)){
                       
                         String slot = getSlotValue(obj ,"ServiceBranch");
                          previousIntent = "GetBranch";
                        
                        
                         if(slot.contains("home")){
                             
                                outputSpeechText = "Your nearest standard bank branch, where you can do home loan is at Barker thirty street next to Rosebank mall";
                                
                             
                         }else if(slot.contains("business")){
                                 outputSpeechText = "Your nearest standard bank branch where you can do business banking is at Rosebank mall";
                                
                         }else if(slot.contains("private")){
                                 outputSpeechText = "Your nearest standard bank branch where you can do private banking is at sandton";
                                
                         }else if(slot.contains("prestige")){
                                outputSpeechText = "Your nearest standard bank branch where you can do prestige banking is at center mall";
                                
                         }
                       
                       }
                        else if ("AMAZON.StopIntent".equals(intentName)) {
                      // Handling for the stop intent goes here.
                        outputSpeechText = "you are welcome, i am here if you need me";
                        outputSpeech.put("shouldEndSession", true);
                        } else {
                                outputSpeechText = "error";
                      }
                   
                        
                    
                        
			outputSpeechType = "PlainText";
		}
		else{
			
			// Blank response for SessionEndRequest
			outputSpeechText = "";
			outputSpeechType = "PlainText";
			
		}
		
		// Construct JSON response package
		JSONObject outputSpeechElement = new JSONObject();
		outputSpeechElement.put("type", outputSpeechType);
		outputSpeechElement.put("text", outputSpeechText);
	
		
		outputSpeech.put("outputSpeech", outputSpeechElement);
		
		JSONObject responseObject = new JSONObject();
		responseObject.put("response", outputSpeech);
                
                JSONObject sessionAttributes = new JSONObject();
                sessionAttributes.put("previousIntent", previousIntent);
                        
                responseObject.put("sessionAttributes", sessionAttributes);
				
		return responseObject;
	}
	
}
