
import javafx.application.Application;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.CheckBox;
import javafx.scene.control.Label;
import javafx.scene.control.ListView;
import javafx.scene.control.RadioButton;
import javafx.scene.control.Tab;
import javafx.scene.control.TabPane;
import javafx.scene.control.TextArea;
import javafx.scene.control.TextField;
import javafx.scene.control.ToggleGroup;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.scene.text.Text;
import javafx.stage.Stage;


/**
 * This is the frontend of the GroupChat application. This JavaFx file gives visuals to the aspects from the GroupChat. It lets users be able to 
 * send messages/replies, react to messages, delete messages, switch users, and see relevant messages. This is done with the use of a ListView holding
 * all the messages. It is cleared when there is an update to the groupchat then the messages will get added again to the ListView and will be redisplayed 
 * with the new update. There is a TextField and a button to be able to select a user. There are radio buttons and a post reaction button to add the reaction
 * to a message. There is also a delete button that users can use to only delete their own messages. There is a textArea for users to type replies and messages
 * in. They can post those with the corresponding buttons. The react, delete, and reply buttons only work if a message is already selected from the groupchat.
 * There is also a checkBox to display only the messages relevant to the user. This will clear all the messages from the groupchat and add the relevant ones
 * and redisplay them. There is a Text element that gives the user some information of the groupchat.
 * 
 * @author Sandeep Dhillon
 *
 */

public class MessengerGUI extends Application{
	private GroupChat theGroupChat;
	private String loggedUser;

	private BorderPane root;
	
	private CheckBox relevantMessages;
	private HBox checkBoxAndLabel;
	private Text chatInfo;
	
	private Tab chooseUserTab;
	private TextField username;
	private Button selectUser;

	private Tab actOnMessagesTab;
	private RadioButton thumbsUp;
	private RadioButton laugh;
	private RadioButton eyeRoll;
	private Button react;
	private Button deleteMessage;
	
	private Tab postMessageTab;
	private TextArea typeMessage;
	private Button replyToMessage;
	private Button postMessage;
	
	private ListView<String> messages;
	private ObservableList<String> items;
	

	@Override
	public void start(Stage primaryStage1) {
		theGroupChat = createGroupChat();
		
		messages = new ListView <String>();
		messages.setPrefHeight(300);
		messages.setPrefWidth(800);
		items = messages.getItems();
		refreshMessages();
		
		
		relevantMessages = new CheckBox();
		relevantMessages.setOnAction(new ShowRelevantMessages());
		Label relevantMessagesText = new Label("Show My Relevant Messages Only");
		checkBoxAndLabel = new HBox (relevantMessages, relevantMessagesText);
		chatInfo = new Text ("Select A User");
		VBox.setMargin(chatInfo, new Insets(5));
		VBox.setMargin(checkBoxAndLabel, new Insets(5,0,0,5));
		checkBoxAndLabel.setDisable(true); 
		
		
		Label enterUser = new Label ("Enter Username:");
		username = new TextField ();		
		selectUser = new Button ("Select");
		selectUser.setOnAction(new SelectUser());
		HBox pickUser = new HBox (7,enterUser, username, selectUser);
		pickUser.setAlignment(Pos.CENTER);;
		chooseUserTab = new Tab("Choose User",pickUser);
		chooseUserTab.setClosable(false);
		
		
		thumbsUp = new RadioButton ("Thumbs Up");
		laugh = new RadioButton ("Ha Ha Ha");
		eyeRoll = new RadioButton ("Eye Roll");
		ToggleGroup reactions = new ToggleGroup();
		thumbsUp.setToggleGroup(reactions);
		laugh.setToggleGroup(reactions);
		eyeRoll.setToggleGroup(reactions);
		thumbsUp.setSelected(true);
		
		react = new Button("React To Message");
		react.setOnAction(new ReactToMessage());
		deleteMessage = new Button ("Delete Message");
		deleteMessage.setOnAction(new DeleteMessage());
		
		VBox theReactions = new VBox (4,thumbsUp, laugh, eyeRoll);
		theReactions.setAlignment(Pos.CENTER_LEFT);
		HBox options = new HBox (theReactions, react, deleteMessage);
		options.setAlignment(Pos.CENTER);
		HBox.setMargin(react, new Insets(0,20,0,10));
		actOnMessagesTab = new Tab ("Act On Message", options);
		actOnMessagesTab.setClosable(false);
		actOnMessagesTab.setDisable(true); 
		
		
		typeMessage = new TextArea ();
		typeMessage.setPrefHeight(80);
		replyToMessage = new Button ("Reply To Message");
		postMessage = new Button ("Post Message");
		postMessage.setOnAction(new PostNewMessage());
		replyToMessage.setOnAction(new ReplyMessage());
		HBox postButtons = new HBox (replyToMessage, postMessage);
		HBox.setMargin(replyToMessage, new Insets(5));
		HBox.setMargin(postMessage, new Insets(5,5,5,0));
		postButtons.setAlignment(Pos.BOTTOM_RIGHT);
		VBox postMessagesBox = new VBox (typeMessage, postButtons);
		postMessageTab = new Tab ("Post Message", postMessagesBox);
		postMessageTab.setClosable(false);
		postMessageTab.setDisable(true); 
		
		TabPane tabGroup = new TabPane();
		tabGroup.getTabs().addAll(chooseUserTab, actOnMessagesTab, postMessageTab);
		VBox.setMargin(tabGroup, new Insets(0,5,0,5));
		
		
		VBox bottomHalf = new VBox (checkBoxAndLabel, chatInfo, tabGroup);
		
		root = new BorderPane();
		root.setCenter(messages);
		root.setBottom(bottomHalf);
		BorderPane.setMargin(messages, new Insets(5,5,0,5));
		
		Scene scene1 = new Scene(root);
		primaryStage1.setTitle("GroupChat GUI");
		primaryStage1.setScene(scene1);
		primaryStage1.show();
	}
	
	private class SelectUser implements EventHandler<ActionEvent>{
		@Override
		public void handle(ActionEvent e) {
			for (String name: theGroupChat.getUserList()) {
				if (username.getText().equals(name)) {
					checkBoxAndLabel.setDisable(false);
					actOnMessagesTab.setDisable(false);
					postMessageTab.setDisable(false);
					loggedUser = username.getText();
					chatInfo.setText("Current User: " + loggedUser);
					if (relevantMessages.isSelected()) {
						refreshRelevantMessages();
					}
					else {
						refreshMessages();
					}	
					return;
				}
			}
			checkBoxAndLabel.setDisable(true);
			actOnMessagesTab.setDisable(true);
			postMessageTab.setDisable(true);
			chatInfo.setText("Not A User");
		}
	}
	
	private class ShowRelevantMessages implements EventHandler<ActionEvent>{
		@Override
		public void handle(ActionEvent e) {
			if (relevantMessages.isSelected()) {
				refreshRelevantMessages();
			}
			else {
				refreshMessages();
			}
		}
	}
	
	private void refreshMessages() {
		items.clear();
		for (String message: theGroupChat.getMessages()) {
			items.add(message);
			messages.scrollTo(items.size());
		}
	}
	
	private void refreshRelevantMessages() {
		items.clear();
		for (String message: theGroupChat.getRelevantMessages(loggedUser)) {
			items.add(message);
			messages.scrollTo(items.size());
		}
	}
	
	
	private class ReactToMessage implements EventHandler<ActionEvent>{
		@Override
		public void handle(ActionEvent e) {
			if (!messages.getSelectionModel().isEmpty()) { 
				int id = Message.getMessageIdFromString(messages.getSelectionModel().getSelectedItem());
				if (thumbsUp.isSelected()) {
					theGroupChat.addReaction(id, Message.Reaction.ThumbsUp);
				}
				if (laugh.isSelected()) {
					theGroupChat.addReaction(id, Message.Reaction.HaHaHa);
				}
				if (eyeRoll.isSelected()) {
					theGroupChat.addReaction(id, Message.Reaction.EyeRoll);
				}
				chatInfo.setText("Reaction Added");
				if (relevantMessages.isSelected()) {
					refreshRelevantMessages();
				}
				else {
					refreshMessages();
				}			
			}
			else {
				chatInfo.setText("A message must be selected");
				
			}	
		}
	}

	private class DeleteMessage implements EventHandler<ActionEvent>{
		@Override
		public void handle(ActionEvent e) {
			if (!messages.getSelectionModel().isEmpty()) {
				int id = Message.getMessageIdFromString(messages.getSelectionModel().getSelectedItem());
				try {
					theGroupChat.deleteMessage(loggedUser, id);
					chatInfo.setText("Message Deleted");
					if (relevantMessages.isSelected()) {
						refreshRelevantMessages();
					}
					else {
						refreshMessages();
					}	
				} catch (IllegalArgumentException exception) {
					chatInfo.setText("Users may only delete their own messages");
				}
						
			}
			else {
				chatInfo.setText("A message must be selected");
			}
		}
	}
	
	private class ReplyMessage implements EventHandler<ActionEvent>{
		@Override
		public void handle (ActionEvent e) {
			if (!typeMessage.getText().isEmpty()) {
				if (!messages.getSelectionModel().isEmpty()) {
					int id = Message.getMessageIdFromString(messages.getSelectionModel().getSelectedItem());
					theGroupChat.postReply(loggedUser, typeMessage.getText(), id);
					typeMessage.setText("");
					if (relevantMessages.isSelected()) {
						refreshRelevantMessages();
					}
					else {
						refreshMessages();
					}
				}
				else {
					chatInfo.setText("A message must be selected");
				}
			}
			else {
				chatInfo.setText("No Text Entered");
			}
		}
	}
	
	private class PostNewMessage implements EventHandler<ActionEvent>{
		@Override
		public void handle (ActionEvent e) {
			if (!typeMessage.getText().isEmpty()) {
				theGroupChat.postMessage(loggedUser, typeMessage.getText());
				typeMessage.setText("");
				if (relevantMessages.isSelected()) {
					refreshRelevantMessages();
				}
				else {
					refreshMessages();
				}			
			}
			else {
				chatInfo.setText("No Text Entered");
			}
		}
	}
	
	
    private GroupChat createGroupChat(){
        GroupChat test = new GroupChat();
        String user1 = "Mike P";
        String user2 = "Steve";
        String user3 = "Boston";
        String user4 = "Irrelevant";
        test.addUser(user1);
        test.addUser(user2);
        test.addUser(user3);
        test.addUser(user4);
        
        test.postMessage(user1, "Hey");
        test.postMessage(user2, "Hello");
        test.addReaction(1, Message.Reaction.HaHaHa);
        test.postMessage(user1, "How are you?");
        test.postMessage(user2, "I'm good");
        test.postMessage(user1, "Wanna hang out, @Boston@?");
        test.addReaction(4, Message.Reaction.ThumbsUp);
        test.addReaction(4, Message.Reaction.ThumbsUp);
        test.postMessage(user2, "Sure let's go");
        test.postReply(user3, "i am busy today", 4);
        return test;
    }
	
}
