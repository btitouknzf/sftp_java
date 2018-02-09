package jp.co.nttdata.shinkin.sftp.SFTP_transfer;

import java.io.IOException;
import java.util.Arrays;
import java.util.Vector;

import com.jcraft.jsch.Channel;
import com.jcraft.jsch.ChannelSftp;
import com.jcraft.jsch.ChannelSftp.LsEntry;
import com.jcraft.jsch.JSch;
import com.jcraft.jsch.JSchException;
import com.jcraft.jsch.Session;
import com.jcraft.jsch.SftpException;
import com.jcraft.jsch.UserInfo;

import jp.co.nttdata.shinkin.sftp.model.SftpUserInfo;

public class SftpSample {
    /** Channel接続タイプ */
    private static final String CHANNEL_TYPE = "sftp";

    /**
     * ファイルアップロード(put)
     * @param hostname 接続先ホスト
     * @param port 接続先ポート
     * @param userId 接続するユーザ
     * @param password 接続するユーザのパスワード
     * @param identityKeyFileName 鍵ファイル名
     * @param sourcePath アップロード対象ファイル(パス付き）<br>
     *            アプリ実行環境上の絶対パスを指定
     * @param destPath アップロード先ファイル（パス付）
     * @throws JSchException Session・Channelの設定/接続エラー時に発生
     * @throws SftpException sftp操作失敗時に発生
     */
    public void putFile(final String hostname, final int port,
            final String userId, final String password,
            final String identityKeyFileName, final String sourcePath,
            final String destPath) throws JSchException, SftpException {

        Session session = null;
        ChannelSftp channel = null;

        try {
            session = connectSession(hostname, port, userId, password,
                    identityKeyFileName);
            channel = connectChannelSftp(session);
            channel.put(sourcePath, destPath);
            if (isExistFile(channel, destPath)) {
                System.out.println("転送成功");
            } else {
                System.out.println("転送先のディレクトリにファイルが存在しません。");
            }
            
            String dirPath = "C:/work/";
            showFileList(channel, dirPath);

        } finally {
            disconnect(session, channel);
        }
    }

    /**
     * ファイル取得(get)
     * @param hostname 接続先ホスト
     * @param port 接続先ポート
     * @param userId 接続するユーザ
     * @param password 接続するユーザのパスワード
     * @param identityKeyFileName 鍵ファイル名
     * @param sourcePath 取得対象ファイルのパス<br>
     *            アプリ実行環境上の絶対パスを指定
     * @param destPath 保存するファイル名（パス付）
     * @throws JSchException Session・Channelの設定/接続エラー時に発生
     * @throws SftpException sftp操作失敗時に発生
     */
    public void getFIle(final String hostname, final int port,
            final String userId, final String password,
            final String identityKeyFileName, final String sourcePath,
            final String destPath) throws JSchException, SftpException {

        Session session = null;
        ChannelSftp channel = null;

        try {
            session = connectSession(hostname, port, userId, password,
                    identityKeyFileName);
            channel = connectChannelSftp(session);
            // ファイル存在確認
            if (isExistFile(channel, destPath)) {
                channel.get(destPath, sourcePath);
            } else {
                System.out.println("ファイル未存在");
            }
        } finally {
            disconnect(session, channel);
        }
    }

    /**
     * Sessionを開始
     * @param hostname 接続先ホスト
     * @param port 接続先ポート
     * @param userId 接続するユーザ
     * @param identityKeyFileName 鍵ファイル名
     * @throws JSchException
     * @throws IOException
     */
    private Session connectSession(final String hostname, final int port,
            final String userId, final String password,
            final String identityKeyFileName) throws JSchException {
        final JSch jsch = new JSch();

        // 鍵ファイル読込（フルパス指定でOK）
        jsch.addIdentity(identityKeyFileName);

        // Session設定
        final Session session = jsch.getSession(userId, hostname, port);
        final UserInfo userInfo = new SftpUserInfo();

        session.setUserInfo(userInfo);
        session.setPassword(password);
        session.connect();

        return session;

    }

    /**
     * SFTPのChannelを開始
     * @param session 開始されたSession情報
     */
    private ChannelSftp connectChannelSftp(
            final Session session) throws JSchException {
        final ChannelSftp channel = (ChannelSftp) session.openChannel(
                CHANNEL_TYPE);
        channel.connect();

        return channel;
    }

    /**
     * Session・Channelの終了
     * @param session 開始されたSession情報
     * @param channels 開始されたChannel情報.複数指定可能
     */
    private void disconnect(final Session session, final Channel... channels) {
        if (channels != null) {
            Arrays.stream(channels).forEach(c -> {
                if (c != null) {
                    c.disconnect();
                }
            });
        }
        if (session != null) {
            session.disconnect();
        }
    }

    /**
     * ファイルの存在確認(stat)
     * @param channel 開始されたChannel情報
     * @param targetFilePath 接続先のパスに該当するファイル
     * @return true 引数に指定されたパスに該当するファイルが存在する場合</br>
     *         false 引数に指定されたパスに該当するファイルが存在しない場合
     */
    public boolean isExistFile(final ChannelSftp channel,
            final String targetFilePath) {
        try {
            channel.lstat(targetFilePath);
        } catch (SftpException e) {
            return false;
        }
        return true;
    }

    /**
     * ファイルリストを取得する（ls-l）
     * @param channel 開始されたChannel情報
     * @param dirPath ディレクトリパス
     * @return true 引数に指定されたパスに該当するファイルが存在する場合</br>
     *         false 引数に指定されたパスに該当するファイルが存在しない場合
     */
    private void showFileList(final ChannelSftp channel,String dirPath) {
        try {
            channel.cd(dirPath);
            Vector list = channel.ls(".");
            System.out.println("---- ls");
            for (int i = 0; i < list.size(); i++) {
                System.out.println(list.get(i));
            }
        } catch (SftpException e) {
            e.printStackTrace();
        }
    }
    
    /**
     * ファイルリストを取得する（ls）(ファイル名のみ)
     * @param channel 開始されたChannel情報
     * @param dirPath ディレクトリパス
     * @return true 引数に指定されたパスに該当するファイルが存在する場合</br>
     *         false 引数に指定されたパスに該当するファイルが存在しない場合
     */
    private void showFileNameList(final ChannelSftp channel,String dirPath) {
        try {
            channel.cd(dirPath);
            Vector list = channel.ls(".");
            for (int i = 0; i < list.size(); i++) {
                LsEntry entry = (LsEntry) list.get(i);
                System.out.println(entry.getFilename());
            }
        } catch (SftpException e) {
            e.printStackTrace();
        }
    }
}
