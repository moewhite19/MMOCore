package cn.whiteg.mmocore.container;

import org.bukkit.command.CommandSender;

public interface Reqest {

    /**
     * 接受时调用
     */
    void accept(CommandSender sender);

    /**
     * 拒绝时调用
     */
    void deny(CommandSender sender);

    /**
     * 取消事件
     */
    void canel();

    /**
     * @return 是否超时
     */
    boolean isOvertime();

    void remove();

    ReqestContainer getHander();

    void setHander(ReqestContainer hander);
}
