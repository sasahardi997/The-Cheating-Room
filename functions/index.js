'use-strict'

const functions = require('firebase-functions');
const admin = require('firebase-admin');
admin.initializeApp(functions.config().firebase);

exports.sendNotification = functions.database.ref('/notifications/{user_id}/{notification_id}').onWrite((change, context) => {

  /*
   * You can store values as variables from the 'database.ref'
   * Just like here, I've done for 'user_id' and 'notification'
   */

   const user_id = context.params.user_id;
   const notification_id = context.params.notification_id;

   console.log('We have a notification to send to : ', context.params.user_id);
   // if(!context.data.val()){
   //  return console.log('A Notification has been deleted from the database: ',notification_id);
   // }

   const fromUser = admin.database().ref(`/notifications/${user_id}/${notification_id}`).once('value');
   return fromUser.then(fromUserResult => {
      const from_user_id = fromUserResult.val().from;
      console.log('You have new notification from : ', from_user_id);

      const userQuery = admin.database().ref(`Users/${from_user_id}/name`).once('value');
            return userQuery.then(userResult =>{
      const userName = userResult.val();

      const deviceToken = admin.database().ref('/Users/'+ user_id +'/device_token').once('value');
          return deviceToken.then(result => {

    const token_id = result.val();

    const payload = {
     notification: {
      title: "Friend Request",
      body: `${userName} has sent you friend request`,
      icon: "avatar.png",
      click_action : "com.myappcompany.hardi.thecheatingroom_TARGET_NOTIFICATION"
     },
     data:{
     	from_user_id : from_user_id
     }
    };

    return admin.messaging().sendToDevice(token_id,payload).then(response => {
     return console.log('This was the notification feature');
    });
   });

      });

   });

});