# PortAPack - 不丢物品的随身背包

## 依赖

本插件依赖于 MySQL 数据库或其等效实现。插件测试于 MariaDB.

## 功能

提供一个底部为可自定义按钮的随身背包（每页可用空间为 45 格）。

## 配置文件

```yaml
# ---------- PortAPack ------------

# 数据库配置
database:
  host: localhost # 数据库地址
  port: 3306 # 数据库端口号
  username: portapack # 数据库用户名，不推荐使用 root!
  password: portapack # 数据库密码
  name: portapack # 数据库名称

# 背包标题配置
# 可用的占位符:
# - $player : 玩家名称
# - $page : 当前页码
# - $total : 总页码
title: $player's PortAPack - $page/$total

# 底部按钮配置
# 按钮的类型：
# PREVIOUS_PAGE : 上一页
# NEXT_PAGE : 下一页
# COMMAND : 命令按钮
# INFO : 信息按钮
buttons:
  0: # 0 为最左下角，编号从左到右依次增大
    icon: STICK # 图标，必须是有效的 Material 名称
    type: PREVIOUS_PAGE # 类型，见上表
    name: < Prev # 按钮名称
    lore:
      - Go to previous page
  1:
    icon: COMMAND
    type: COMMAND
    name: Say hi
    lore:
      - Just say hi
    command: say hi # 以玩家身份执行的命令
  7:
    icon: DIAMOND
    type: INFO
    name: Have fun!
    lore:
      - Brought to you by dousha99
      - Consider buying me a cup of coffe? :P
  8:
    icon: STICK
    type: NEXT_PAGE
    name: Next >
    lore:
      - Go to next page

# 权限设置
freePage: 1 # 免费开放页数
maxPage: 5 # 最大页数
pagePerm:
  2: portapack.donation # 具有该权限的玩家可以访问最多到此页
  3: portapack.premium
  5: portapack.luxary

# 技术设置
watchdogTolerance: 1000 # 服务器无响应阈(yu4)值，单位 ms
verifyOnStartup: false # 在启动时检查所有玩家的背包状态
```

## 命令与权限

所有命令均以 `prt` 开头，之后的第一个字母为操作对象，第二个字母为操作行为。具体列表如下：

```yaml
commands:
  prtjp:
    usage: /prtjp <玩家> [条目数量 = 5]
    description: 打印该玩家的最近 n 条日志
    permission: portapack.jounral.read
  prtjv:
    usage: /prtjv <玩家>
    description: 校验玩家背包（校验时将锁定玩家背包）
    permission: portapack.journal.verify
  prtsw:
    usage: /prtsw <玩家> [时间 = 现在] [保留日志 = true]
    description: 保存玩家背包截至给定时间的快照（如果不指定时间，则直接复制当前背包内容）
    permission: portapack.snapshot.write
  prtsp:
    usage: /prtsp <玩家> [条目数量 = 5]
    description: 打印该玩家的所有快照
    permission: portapack.snapshot.read
  prtso:
    usage: /prtso <玩家> [快照时间 = 最后一次快照]
    description: 将玩家背包回滚到快照状态
    permission: portapack.snapshot.open
  prtio:
    usage: /prtio [玩家 = 自己]
    description: 打开背包
    permission: portapack.inventory.open
  prtix:
    usage: /prtix [玩家 = 自己] [页码 = 全部]
    description: 清空某页背包，如不页码，则直接清空所有内容，该操作不会写入日志！
    permission: protapack.inventory.clear
```

## 使用注意

* 不要把 maxPage 设置到比 pagePerm 中指定的最小值之下！
* 不要在用一段时间之后降低 maxPage 或 pagePerm 中指定的值，除非你确定没人用之后的页数！
* 不要用诸如 PlugMan 等工具动态重载此插件！动态重载可能会导致数据损坏！（见下文「理解数据一致性」）
* 不要使用数据库管理工具直接删除早期日志！（见下文「理解日志」）

## 稍微带点技术的内容

### 理解数据一致性

### 理解日志

日志是物品栏操作的记录，也是验证物品栏状态的工具。

#### 日志的输出

日志条目的输出格式为：

```text
#编号 日期 时间@玩家: 操作类型, 页码.格号: 物品名 x 个数
```

### 理解快照

快照是从日志或当前背包状态创建的可持久化的数据结构。

#### 快照的创建

快照在默认情况下会复制当前背包的状态，并保存到 `$data/snapshot/玩家名/latest.yml`.

如果指定了时间，则会保存到 `$data/snapshot/玩家名/给定时间.yml`

#### 管理快照

如果已经创建了 `latest.yml`, 可以删除早期的快照文件。
