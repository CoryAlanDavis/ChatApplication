package application;



import java.io.IOException;
import java.util.Optional;

import javafx.beans.binding.Bindings;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.control.*;
import javafx.scene.control.Alert.AlertType;
import javafx.scene.input.KeyCode;
import javafx.scene.input.KeyEvent;
import javafx.scene.text.Font;
import javafx.scene.text.Text;
import javafx.scene.text.TextFlow;
;

public class ClientController {
	
	
	@FXML Button MessageSendButton;
	@FXML TextField userTextfield;
	@FXML MenuBar menuBar;
	@FXML MenuItem menuBarFriend;
	@FXML MenuItem menuBarOptions;
	@FXML MenuItem menuBarHelp;
	@FXML TabPane tabPane;
	@FXML ListView<String> friendsList;
	
	public static final ObservableList<String> friendsObList = FXCollections.observableArrayList();
	
	public void sendMessage() {
		
		if(userTextfield.getText() != null) {
			//insert send message net code here
			 if(tabPane.getTabs().size() > 0 ) {
				Text sentMessage = new Text (userTextfield.getText() + "\n");
				sentMessage.setFont(Font.font("Verdana",16));
				
				ScrollPane tempScroll = (ScrollPane)tabPane.getSelectionModel().getSelectedItem().getContent();
				TextFlow tempTextFlow = (TextFlow) tempScroll.getContent();
				tempTextFlow.getChildren().add(sentMessage);
				tempScroll.setVvalue(1.0);
			 }
			 else {
				 //dialog box informing user to open a tab to talk to a friend
				Alert alert = new Alert(AlertType.INFORMATION);
				alert.setTitle("Oops!");
				alert.setHeaderText("You must first open a tab!");
				alert.setContentText("To open a message tab, right click a name from the friends list and select 'message'.");
				alert.showAndWait();
			 }
			
			 userTextfield.setText(null);
			
			
		}
		
		
	
		
	}
	
	
	public void textFieldEnterPressed(KeyEvent event) {
		if(event.getCode() == KeyCode.ENTER) {
			sendMessage();
		}
			
	}
	
	
	public void addFriend() {
		
		//Sets up a dialog to ask for friend's Id
		TextInputDialog friendDialog = new TextInputDialog("Friend ID");
		friendDialog.setTitle("Add a Friend");
		friendDialog.setHeaderText("Add a friend");
		friendDialog.setContentText("Please enter your friends user ID:");
		Optional<String> friendId = friendDialog.showAndWait();
		
		if(friendId.isPresent()){
			Boolean alreadyFriends = false;
			//net code to check the user id with user database
			String result = friendId.get();
			
			for(String friend : friendsObList) {
				if(friend.equals(result)) {
					Alert alert = new Alert(AlertType.INFORMATION);
					alert.setTitle("Already Friends");
					alert.setHeaderText("You are already friends with " + result + ".");
					alert.showAndWait();
					alreadyFriends = true;
					break;
				}
			}
			if(alreadyFriends.equals(false)) {
				friendsObList.add(result);
				friendsList.setItems(friendsObList);
				setupFriendCells();
			}
			
			
			
		}
		
	}
	
	public void setupFriendCells() {
		
		friendsList.setCellFactory(lv -> {
			ListCell<String> cell = new ListCell<String>();
			ContextMenu contextMenu = new ContextMenu();
			
			//creating a message MenuItem for all nonEmpty cells of friends list
			MenuItem messageItem = new MenuItem();
			messageItem.textProperty().bind(Bindings.format("Message \"%s\"", cell.itemProperty()));
			messageItem.setOnAction(event ->{
				//will send to new function to setup new tab. 
				System.out.println("Start Message with " + cell.getItem());
				addMessageTab(cell.getItem());
			});
			
			//creating a remove friend MenuItem for all nonEmpty cells of friends list
			MenuItem removeItem = new MenuItem();
			removeItem.textProperty().bind(Bindings.format("Remove Friend \"%s\"", cell.itemProperty()));
			removeItem.setOnAction(event ->{
				
				//Alert to make sure the user truly wants to remove friend
				Alert alert = new Alert(AlertType.CONFIRMATION);
				alert.setTitle("Remove Friend Confirmation");
				alert.setHeaderText("Remove " + cell.getItem() + " as a friend?");
				Optional<ButtonType> removalResult = alert.showAndWait();
				
				//removes selected friend from friends list
				if(removalResult.get() == ButtonType.OK) {
					//add in net code to remove friend in database
					
					//remove tab of removed friend
					Tab removeThisTab = null;
					for(Tab tab : tabPane.getTabs()) {
						if(tab.getText().equals(cell.getItem())) {
							removeThisTab = tab;
						}
						
					}
					if(removeThisTab != null) {
						tabPane.getTabs().remove(removeThisTab);
					}
					
					friendsObList.remove(cell.getIndex());
				}
				
			});
			
			//adds message and remove friend MenuItems 
			contextMenu.getItems().addAll(messageItem,removeItem);
			
			cell.textProperty().bind(cell.itemProperty());
			
			cell.emptyProperty().addListener((obs,wasEmpty,isNowEmpty) -> {
				if(isNowEmpty) {
					cell.setContextMenu(null);
				}
				else {
					cell.setContextMenu(contextMenu);
				}
			});
			return cell;
			
		});
	}
	
	public void addMessageTab(String Id) {
		Boolean alreadyExist =  false;
		
		for(Tab tab : tabPane.getTabs()) {
			if(tab.getText() == Id) {
				tabPane.getSelectionModel().select(tab);
				alreadyExist = true;
				
			}
		} //end for
		
		if (alreadyExist == false) {
			try {
				Tab newTab = (Tab)FXMLLoader.load(this.getClass().getResource("tabTemplate.fxml"));
				newTab.setText(Id);
				newTab.setId(Id);
				
				tabPane.getTabs().addAll(newTab);
				tabPane.getSelectionModel().select(newTab);
			}
			catch(IOException e) {
				System.out.println(e);
			}
		}
	}//end addMessageTab
	
}
