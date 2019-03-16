const functions = require('firebase-functions');
const admin = require('firebase-admin');
admin.initializeApp();

exports.sendChatNotifications = functions.firestore
    .document('chats/{from}/{to}/{messageId}')//chats/{chatId}/messages/{messageId}  ->  chats/{from}/{to}/{messageId}
    .onCreate((snap, context) => {
      // Get an object with the current document value.
      // If the document does not exist, it has been deleted.
      const document = snap.exists ? snap.data() : null;

      if (document) {
        var message = {
          notification: {
            title: 'NEW MESSAGE',//document.from + ' sent you a message',
            body: 'HAS ARRIVED'//document.text
          },
          topic: 'chats'//context.params.chatId
        };

        return admin.messaging().send(message)
          .then((response) => {
            // Response is a message ID string.
            console.log('Successfully sent message:', response);
            return response;
          })
          .catch((error) => {
            console.log('Error sending message:', error);
            return error;
          });
      }

      return "document was null or empty";
    });

exports.addTimeStamp = functions.firestore
    .document('chats/{from}/{to}/{messageId}')//chats/{chatId}/messages/{messageId}  ->  chats/{from}/{to}/{messageId}
    .onCreate((snap, context) => {
      if (snap) {
        return snap.ref.update({
                    timestamp: admin.firestore.FieldValue.serverTimestamp()
                });
      }

      return "snap was null or empty";
    });