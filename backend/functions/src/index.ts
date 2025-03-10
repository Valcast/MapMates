/* eslint-disable max-len */
import * as functions from "firebase-functions";
import * as admin from "firebase-admin";
import {Timestamp} from "firebase-admin/firestore";

admin.initializeApp();

export const sendNotificationEventCreated = functions.firestore.onDocumentCreated({
  document: "events/{eventId}",
  region: "europe-central2",
}, async (snap) => {
  const authorId = snap.data?.get("author");

  const authorDoc = await admin.firestore().collection("users").doc(authorId).get();

  const authorFollowers = authorDoc.get("followers");

  for (const follower of authorFollowers) {
    await admin.firestore().collection(`users/${follower}/notifications`).add({
      type: "EVENT_CREATED",
      timestamp: Timestamp.now(),
      read: false,
      data: {
        eventId: snap.data?.id,
        authorId,
      },
    });
  }

  return null;
});


