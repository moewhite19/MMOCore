# MMOCore Minecraft基础插件

<p>Be used for Server mc.whiteg.cn</p>
<p>因为觉得以前提交的不太规范，于是把账户里的项目全都Reset了一下，重新开始提交</p>

主指令:mmocore mmo



如果子指令有添加到plugin.yml那么子指令可无需主指令也能直接使用


子指令:


confirm c: 用于接受请求（传送请求等）

deny d: 用于拒接请求

imop: 导入Ess基础插件数据

suicide: 自杀 需要权限mmo.suicide

数据管理指令: userdata ud

均需要权限节点 whiteg.test

clearup: 清理数据回收站 用法/ud clearup <天数>

create: 创建玩家数据

delete: 将玩家数据移动到回收站(如果回收站有该玩家的数据则是优先删除回收站数据)

fixfilename: 修复文件名（调试用）

get: 获取玩家数据指定节点的数据

set: 设置玩家指定节点数据

getuuid: 获取玩家uuid

list: 获取玩家节点列表数据

load: 加载玩家数据

unload: 卸载玩家数据

reload: 重载玩家数据

rename: 给玩家重命名

save: 立即储存玩家数据

clearplayerdata: 检查并清理没有插件数据的玩家
