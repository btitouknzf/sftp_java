package jp.co.nttdata.shinkin.sftp.model;

import com.jcraft.jsch.UserInfo;

/**
 * SFTPに接続するユーザ情報を保持するクラス
 */
public class SftpUserInfo implements UserInfo {

    public String getPassphrase() {
        // TODO 自動生成されたメソッド・スタブ
        return null;
    }

    public String getPassword() {
        // TODO 自動生成されたメソッド・スタブ
        return null;
    }

    public boolean promptPassphrase(String arg0) {
        // TODO 自動生成されたメソッド・スタブ
        return false;
    }

    public boolean promptPassword(String arg0) {
        // TODO 自動生成されたメソッド・スタブ
        return true;
    }

    public boolean promptYesNo(String arg0) {
        // TODO 自動生成されたメソッド・スタブ
        return true;
    }

    public void showMessage(String arg0) {
        // TODO 自動生成されたメソッド・スタブ

    }

}
