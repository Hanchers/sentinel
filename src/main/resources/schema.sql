
-- 服务节点表
CREATE TABLE IF NOT EXISTS service_node (
    id INTEGER PRIMARY KEY AUTOINCREMENT,
    name TEXT NOT NULL, -- 服务节点名称
    remark TEXT, -- 备注
    status TEXT NOT NULL default 'down', -- 节点状态，枚举：down-不可用、ok-可用
    cluster_id INTEGER NOT NULL, -- 服务集群唯一标识，控制依赖的基本单位
    health_check_method TEXT NOT NULL, -- 健康检查方法、类型,枚举:http、bash
    health_check_cmd TEXT, -- 健康检查命令
    restart_method TEXT NOT NULL, -- 重启类型、方法,枚举:bash
    restart_cmd TEXT, -- 重启命令
    create_time TEXT NOT NULL default (DATETIME('now', 'localtime')),
    update_time TEXT NOT NULL default (DATETIME('now', 'localtime'))
);

-- 服务集群表
CREATE TABLE IF NOT EXISTS service_cluster (
    id INTEGER PRIMARY KEY AUTOINCREMENT,
    name TEXT NOT NULL, -- 服务节点名称
    remark TEXT, -- 备注
    status TEXT NOT NULL default 'down', -- 节点状态，枚举：down-不可用、up-达到最小存活数量、ok-全部服务可用、wait-等待依赖恢复、
    min_alive_num INTEGER NOT NULL default 1, -- 服务集群最小存活数量
    depend_clusters NOT NULL default '0',
    create_time TEXT NOT NULL default (DATETIME('now', 'localtime')),
    update_time TEXT NOT NULL default (DATETIME('now', 'localtime'))
);

-- 依赖的有向无环图
CREATE TABLE IF NOT EXISTS dependent_dag (
    id INTEGER PRIMARY KEY AUTOINCREMENT,
    source_cluster_id INTEGER NOT NULL default '0',
    target_cluster_id INTEGER NOT NULL default '-1',
    create_time TEXT NOT NULL default (DATETIME('now', 'localtime')),
    update_time TEXT NOT NULL default (DATETIME('now', 'localtime')),
    UNIQUE(source_cluster_id, target_cluster_id)
);

