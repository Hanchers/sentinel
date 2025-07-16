-- 集群
INSERT OR IGNORE INTO service_cluster (id,name,remark,status,min_alive_num,depend_clusters,create_time,update_time) VALUES
	 (1,'a_cluster',NULL,'down',1,'0','2025-06-09 18:17:45','2025-06-18 14:49:59'),
	 (2,'b_cluster',NULL,'down',1,'1','2025-06-18 14:50:17','2025-06-18 14:50:17'),
	 (3,'c_cluster',NULL,'down',1,'0','2025-06-18 14:51:58','2025-06-18 14:51:58'),
	 (4,'d_cluster',NULL,'down',1,'3','2025-06-18 14:51:58','2025-06-18 14:51:58'),
	 (5,'e_cluster',NULL,'down',1,'3','2025-06-18 14:51:58','2025-06-18 14:51:58'),
	 (6,'f_cluster',NULL,'down',1,'2,4,5','2025-06-18 14:51:58','2025-06-18 14:51:58');

-- 服务节点
INSERT OR IGNORE INTO service_node (id,name,remark,status,cluster_id,health_check_method,health_check_cmd,restart_method,restart_cmd,create_time,update_time) VALUES
	 (1,'a_service','a服务','down',1,'bash','ping -c 4 baidu.com','bash','ping -c 4 baidu.com','2025-06-18 15:17:47','2025-06-18 15:17:47'),
	 (2,'b_service','b服务','down',2,'bash','ping -c 4 baidu.com','bash','ping -c 4 baidu.com','2025-06-18 15:20:50','2025-06-18 15:20:50'),
	 (3,'c_service','c服务','down',3,'bash','ping -c 4 baidu.com','bash','ping -c 4 baidu.com','2025-06-18 15:17:47','2025-06-18 15:17:47'),
	 (4,'d_service','d服务','down',4,'bash','ping -c 4 baidu.com','bash','ping -c 4 baidu.com','2025-06-18 15:20:50','2025-06-18 15:20:50'),
	 (5,'e_service','e服务','down',5,'bash','ping -c 4 baidu.com','bash','ping -c 4 baidu.com','2025-06-18 15:17:47','2025-06-18 15:17:47'),
	 (6,'f_service','f服务','down',6,'bash','ping -c 4 baidu.com','bash','ping -c 4 baidu.com','2025-06-18 15:20:50','2025-06-18 15:20:50');


-- 集群依赖关系
INSERT OR IGNORE INTO dependent_dag (id,source_cluster_id,target_cluster_id,create_time,update_time) VALUES
	 (1,'0','1','2025-06-18 14:59:20','2025-06-18 14:59:20'),
	 (2,'1','2','2025-06-18 14:59:20','2025-06-18 14:59:20'),
	 (3,'0','3','2025-06-18 14:59:20','2025-06-18 14:59:20'),
	 (4,'3','4','2025-06-18 14:59:20','2025-06-18 14:59:20'),
	 (5,'3','5','2025-06-18 14:59:20','2025-06-18 14:59:20'),
	 (6,'2','6','2025-06-18 14:59:20','2025-06-18 14:59:20'),
	 (7,'4','6','2025-06-18 14:59:20','2025-06-18 14:59:20'),
	 (8,'5','6','2025-06-18 14:59:20','2025-06-18 14:59:20'),
	 (9,'6','-1','2025-06-18 14:59:20','2025-06-18 14:59:20');
