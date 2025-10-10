package com.hancher.sentinel.controller;

import com.hancher.sentinel.entity.ServiceCluster;
import com.hancher.sentinel.entity.ServiceNode;
import com.hancher.sentinel.enums.DagNodeTypeEnum;
import com.hancher.sentinel.service.ServiceClusterService;
import com.hancher.sentinel.service.ServiceNodeService;
import com.hancher.sentinel.web.vo.DagData;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

/**
 * 首页
 * @date 2025-07-07 10:23:53
 * @author hancher
 * @since 1.0
 */
@Controller
@RequestMapping("/dag")
@AllArgsConstructor
public class DagController {
    private ServiceClusterService clusterService;
    private ServiceNodeService nodeService;

    /**
     * 服务依赖 概览
     * @return
     */
    @GetMapping("/overview")
    public String overview(Model model) {
        // 查询所有的集群
        List<ServiceCluster> allCluster = clusterService.list();
        // 查询所有的集群节点
        List<ServiceNode> allNode = nodeService.list();

        Map<Long, List<ServiceNode>> nodeMap = allNode.stream().collect(Collectors.groupingBy(ServiceNode::getClusterId));

        int total = allCluster.size() + allNode.size();
        List<DagData.DagNode> nodes = new ArrayList<>(total);
        List<DagData.DagEdge> edges = new ArrayList<>(total);

        nodes.add(buildDagNode(new ServiceCluster().setId(0L).setName("开始")));
        for (ServiceCluster cluster : allCluster) {
            nodes.add(buildDagNode(cluster));

            // 集群下属节点
            List<ServiceNode> clusterNodes = nodeMap.getOrDefault(cluster.getId(), List.of());
            for (ServiceNode node : clusterNodes) {
                nodes.add(buildDagNode(node));
            }
            // 构建集群边
            String clusterIdStr = cluster.getId().toString();
            for (String start : cluster.getDependClusters()) {
                edges.add(buildDagClusterEdge(start,clusterIdStr));

            }

            // 构建节点边
            for (ServiceNode node : clusterNodes) {
                edges.add(buildDagNodeEdge(node.getId().toString(),clusterIdStr));
            }
        }

        model.addAttribute("dagData", DagData.builder().nodes(nodes).edges(edges).build());
        return "dag/overview";
    }


    private DagData.DagNode buildDagNode(ServiceCluster cluster) {
        return DagData.DagNode.builder()
                .id(DagNodeTypeEnum.cluster.name()+"-"+cluster.getId())
                .label(cluster.getName())
                .size(50)
                .nodeType(DagNodeTypeEnum.cluster)
                .build();
    }

    private DagData.DagNode buildDagNode(ServiceNode node) {
        return DagData.DagNode.builder()
                .id(DagNodeTypeEnum.node.name()+"-"+node.getId())
                .label(node.getName())
                .size(30)
                .nodeType(DagNodeTypeEnum.node)
                .build();
    }


    /**
     * 构建节点边，指向集群
     * @param source id
     * @param target id
     * @return node-id -> cluster-id
     */
    private DagData.DagEdge buildDagNodeEdge(String source,String target) {
        return DagData.DagEdge.builder().source(DagNodeTypeEnum.node.name()+"-"+source).target(DagNodeTypeEnum.cluster.name()+"-"+target).build();
    }

    /**
     * 构建集群边
     * @param source id
     * @param target id
     * @return cluster-id -> cluster-id
     */
    private DagData.DagEdge buildDagClusterEdge(String source,String target) {
        return DagData.DagEdge.builder().source(DagNodeTypeEnum.cluster.name()+"-"+source).target(DagNodeTypeEnum.cluster.name()+"-"+target).build();
    }
}
