package com.finalsoft.contactsChanges;

import android.annotation.SuppressLint;

import org.telegram.messenger.NotificationCenter;
import org.telegram.messenger.UserConfig;
import org.telegram.tgnet.TLRPC;

public class UpdateBiz {
   private DBHelper dba = new DBHelper();

   @SuppressLint({"DefaultLocale"})
   private String formatUserSearchName(String var1, String var2, String var3) {
      StringBuilder var4 = new StringBuilder("");
      if(var2 != null && var2.length() > 0) {
         var4.append(var2);
      }

      if(var3 != null && var3.length() > 0) {
         if(var4.length() > 0) {
            var4.append(" ");
         }

         var4.append(var3);
      }

      if(var1 != null && var1.length() > 0) {
         var4.append(";;;");
         var4.append(var1);
      }

      return var4.toString().toLowerCase();
   }

   public boolean insertUpdate(TLRPC.User currentUser, TLRPC.Update baseUpdate) {
      boolean var4 = false;
      boolean var3 = var4;
//      Log.w("sina-msg","it is runing");

//      if (baseUpdate instanceof TLRPC.TL_updateUserName) {
//         TLRPC.TL_updateUserName sinaUpdate = (TLRPC.TL_updateUserName) baseUpdate;
//         isCanCast = true;
//         sUser = getUser(sinaUpdate.user_id);
//      }else if(baseUpdate instanceof TLRPC.TL_updateUserPhone){
//         TLRPC.TL_updateUserPhone sinaUpdate = (TLRPC.TL_updateUserPhone) baseUpdate;
//         isCanCast = true;
//         sUser = getUser(sinaUpdate.user_id);
//      }else if(baseUpdate instanceof TLRPC.TL_updateUserPhoto){
//         TLRPC.TL_updateUserPhoto sinaUpdate = (TLRPC.TL_updateUserPhoto) baseUpdate;
//         isCanCast = true;
//         sUser = getUser(sinaUpdate.user_id);
//      }

     // TLRPC.TL_updateUserName updateUserName = (TLRPC.TL_updateUserName) update;
      if(currentUser.id != UserConfig.getInstance(UserConfig.selectedAccount).getClientUserId()) {
         if(currentUser == null) {
            var3 = var4;
         } else {
            UpdateModel var5 = new UpdateModel();
            var5.setUserId(currentUser.id);
            var5.setNew(true);
            if(baseUpdate instanceof TLRPC.TL_updateUserName) {
              TLRPC.TL_updateUserName updateUserName = (TLRPC.TL_updateUserName) baseUpdate;
               var5.setOldValue(this.formatUserSearchName(currentUser.username, currentUser.first_name, currentUser.last_name));
               var5.setNewValue(this.formatUserSearchName(updateUserName.usernames.get(0).username, updateUserName.first_name, updateUserName.last_name));
               var5.setType(2);
            } else if(baseUpdate instanceof TLRPC.TL_updateUserPhone) {
               TLRPC.TL_updateUserPhone updateUserPhone = (TLRPC.TL_updateUserPhone) baseUpdate;
               var5.setOldValue(currentUser.phone);
               var5.setNewValue(updateUserPhone.phone);
               var5.setType(4);
            } else {
               var3 = var4;
               if(!(baseUpdate instanceof TLRPC.TL_updateUserPhoto)) {
                  return var3;
               }
               var5.setType(3);
            }

            dba.a(var5);
            NotificationCenter.getInstance(UserConfig.selectedAccount).postNotificationName(NotificationCenter.mainUserInfoChanged, new Object[0]);
            var3 = true;
         }
      }

      return var3;
   }
}
