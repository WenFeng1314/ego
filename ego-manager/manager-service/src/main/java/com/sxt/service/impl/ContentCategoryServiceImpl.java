package com.sxt.service.impl;

import com.alibaba.dubbo.config.annotation.Service;
import com.alibaba.fastjson.JSON;
import com.sxt.domain.ContentCategory;
import com.sxt.domain.ContentCategoryExample;
import com.sxt.domain.ContentCategory;
import com.sxt.domain.ContentCategoryExample;
import com.sxt.mapper.ContentCategoryMapper;
import com.sxt.service.ContentCategoryService;
import com.sxt.utils.TreeNode;
import org.springframework.beans.factory.annotation.Autowired;
import redis.clients.jedis.Jedis;
import redis.clients.jedis.JedisPool;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

/**
 * @author WWF
 * @title: ContentCategoryServiceImpl
 * @projectName ego
 * @description: com.sxt.service.impl
 * @date 2019/5/22 21:29
 */
@Service
public class ContentCategoryServiceImpl implements ContentCategoryService {
    @Autowired
    private ContentCategoryMapper dao;
    @Autowired
    private JedisPool jedisPool;
    private static final String MENU_KEY="EGO-SHOP-ALL-MENU";

    @Override
    public List<TreeNode> findByPid(Integer pid) {
        ContentCategoryExample contentCategoryExample = new ContentCategoryExample();
        ContentCategoryExample.Criteria criteria = contentCategoryExample.createCriteria();
        criteria.andParentIdEqualTo(pid.longValue());
        List<ContentCategory> contentCategories = dao.selectByExample(contentCategoryExample);
         List<TreeNode> trees=new ArrayList<TreeNode>();
        for (ContentCategory contentCategory : contentCategories) {
                trees.add(contentCategory2TreeNode(contentCategory));

        }

        return trees;
    }

    /**
     * 新增一个节点
     * @param pid
     * @param name
     */
    @Override
    public void addNode(Integer pid, String name) {
        //创建一个节点，分类
        ContentCategory contentCategory = new ContentCategory();
        contentCategory.setParentId(pid.longValue());
        contentCategory.setName(name);
        contentCategory.setSortOrder(1);
        contentCategory.setStatus(1);
        contentCategory.setUpdated(new Date());
        contentCategory.setCreated(new Date());
        contentCategory.setIsParent(false);
        //新增一个节点，对象之前的节点，有什么影响
        //1.是子节点的变为父节点
        int result = dao.insert(contentCategory);
        if (result>0){
            //根据pid去查询节点，它是什么节点
            ContentCategory parent = dao.selectByPrimaryKey(pid.longValue());
            if (parent.getIsParent()==false){
                parent.setIsParent(true);
                dao.updateByPrimaryKey(parent);
            }
        }

    }

    @Override
    public void updateNode(Integer id, String name) {
        ContentCategory contentCategory = new ContentCategory();
        contentCategory.setId(id.longValue());
        contentCategory.setName(name);
        dao.updateByPrimaryKey(contentCategory);

    }

    /**
     * 删除一个节点
     * @param id
     */
    @Override
    public Integer deleteNode(Integer id) {
        ContentCategory contentCategory = dao.selectByPrimaryKey(id.longValue());
        Boolean isParent = contentCategory.getIsParent();
        if (isParent){
            //递归删除可能会发送大量的sql，我们可以这样解决
            //我们可以根据条件sql语句，一下子，删除所有的，子节点
            List<Long> deleteIds=getAllChildId(id);
            //再把父节点id也添加进去，
            deleteIds.add(id.longValue());
            ContentCategoryExample contentCategoryExample = new ContentCategoryExample();
            ContentCategoryExample.Criteria criteria = contentCategoryExample.createCriteria();
            criteria.andIdIn(deleteIds);
            int result = dao.deleteByExample(contentCategoryExample);
            return result;

        }else {
            //再判断，该父节点，有几个子节点
            ContentCategoryExample contentCategoryExample = new ContentCategoryExample();
            ContentCategoryExample.Criteria criteria = contentCategoryExample.createCriteria();
            criteria.andParentIdEqualTo(id.longValue());
            List<ContentCategory> contentCategories = dao.selectByExample(contentCategoryExample);
            if (contentCategories.size()>1){
                //直接删除该节点，父节点不受影响
                int result = dao.deleteByPrimaryKey(id.longValue());
                return result;

            }else {
                //删除该节点，并把该父节点，变为子节点
                ContentCategory contentCategory1 = new ContentCategory();
                contentCategory1.setId(contentCategory.getParentId());
                contentCategory1.setIsParent(false);
                int reuslt = dao.deleteByPrimaryKey(id.longValue());
                return reuslt;
            }
        }
    }


    /**
     * 根据id去得到所有的儿子的id
     * @param pid
     * @return
     */
    private List<Long> getAllChildId(Integer pid) {
        ContentCategoryExample contentCategoryExample = new ContentCategoryExample();
        ContentCategoryExample.Criteria criteria = contentCategoryExample.createCriteria();
        criteria.andParentIdEqualTo(pid.longValue());
        List<ContentCategory> contentCategories = dao.selectByExample(contentCategoryExample);
        //创建一个list集合，装所有儿子的id和自己的id
        List<Long> ids=new ArrayList<Long>();
        for (ContentCategory contentCategory: contentCategories) {
            ids.add(contentCategory.getId());
        }
        return ids;
    }

    /**
     * contentCategory2TreeNode
     * @param contentCategory
     * @return
     */
    private TreeNode contentCategory2TreeNode(ContentCategory contentCategory) {
        TreeNode treeNode = new TreeNode();
        treeNode.setId(contentCategory.getId().toString());
        treeNode.setName(contentCategory.getName());
        treeNode.setParent(contentCategory.getIsParent()?true:false);

        return treeNode;
    }

    /**
     * 一次加载所有的树
     * @return
     */
    @Override
    public List<TreeNode> loadAllTree() {
        Jedis jedis = jedisPool.getResource();
        List<TreeNode> treeNodes=null;
        try {
            if (jedis.exists(MENU_KEY)){//从缓存里面加载
                String json = jedis.get(MENU_KEY);
                treeNodes = JSON.parseArray(json, TreeNode.class);

            }else {//从数据库里面查询
                treeNodes = loadAllTree((short)0);
                //将数据放入缓存里面
                jedis.set(MENU_KEY,JSON.toJSONString(treeNodes));

            }
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            jedis.close();
        }
        return treeNodes;
    }
    // 我们的树里面包含完整的子节点
    public List<TreeNode> loadAllTree(Short id) {
        ContentCategoryExample contentCategoryExample = new ContentCategoryExample();
        contentCategoryExample.createCriteria().andParentIdEqualTo(id.longValue());
        List<ContentCategory> contentCategories = dao.selectByExample(contentCategoryExample);
        // 是一级节点
        List<TreeNode> trees = new ArrayList<TreeNode>();
        for (ContentCategory contentCategory : contentCategories) {
            TreeNode treeNode = contentCategory2TreeNode(contentCategory);
            //开始递归了
            treeNode.setChildren(loadAllTree(contentCategory.getId().shortValue()));
            // 将子节点也加载进去
            trees.add(treeNode);
        }
        return trees;

    }
    }
