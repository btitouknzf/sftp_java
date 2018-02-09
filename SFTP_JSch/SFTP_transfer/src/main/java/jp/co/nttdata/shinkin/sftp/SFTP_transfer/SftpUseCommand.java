package jp.co.nttdata.shinkin.sftp.SFTP_transfer;

import java.io.IOException;
import java.util.Arrays;

import com.jcraft.jsch.Channel;
import com.jcraft.jsch.ChannelExec;
import com.jcraft.jsch.JSch;
import com.jcraft.jsch.JSchException;
import com.jcraft.jsch.Session;
import com.jcraft.jsch.SftpException;
import com.jcraft.jsch.UserInfo;

import jp.co.nttdata.shinkin.sftp.model.SftpUserInfo;

public class SftpUseCommand {

    /** cpコマンドのテンプレート文字列 */
    private static final String CP_COMMAND_TEMPLATE = "cp -p %s %s";

    /** ssh実行の接続タイプ */
    private static final String CHANNEL_TYPE_EXEC = "exec";

    /**
     * ファイルコピー コマンド発行版
     * @param hostname 接続先ホスト
     * @param port 接続先ポート
     * @param userId 接続するユーザ
     * @param password 接続するユーザのパスワード
     * @param identityKeyFileName 鍵ファイル名
     * @param sourcePath コピー元ファイルのパス
     * @param destPath コピー先のパス
     * @throws JSchException Session・Channelの設定/接続エラー時に発生
     * @throws SftpException sftp操作失敗時に発生
     */
    public void putFileUseCommand(final String hostname,
                                final int port,
                                final String userId,
                                final String password,
                                final String identityKeyFileName,
                                final String sourcePath,
                                final String destPath) throws JSchException, SftpException {
        Session session = null;
        ChannelExec channel = null;
        
        try {
            session = connectSession(hostname, port, userId, password, identityKeyFileName);
            channel = (ChannelExec) session.openChannel(CHANNEL_TYPE_EXEC);
            channel.setCommand(String.format(CP_COMMAND_TEMPLATE, sourcePath,destPath));
            channel.connect();
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
            final String identityKeyFileName) throws JSchException{
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
    
}
