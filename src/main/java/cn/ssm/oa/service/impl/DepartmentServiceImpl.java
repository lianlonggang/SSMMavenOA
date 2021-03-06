package cn.ssm.oa.service.impl;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;

import cn.ssm.oa.mapper.DepartmentMapper;
import cn.ssm.oa.po.Department;
import cn.ssm.oa.service.DepartmentService;
import tk.mybatis.mapper.entity.Example;
import tk.mybatis.mapper.entity.Example.Criteria;

public class DepartmentServiceImpl implements DepartmentService {

	@Autowired
	private DepartmentMapper departmentMapper;
	
	@Override
	public List<Department> findAll() throws Exception {
		List<Department> departments = departmentMapper.selectByExample(null);
		for (Department department : departments) {
			Department parent = departmentMapper.selectByPrimaryKey(department.getParentId());
			department.setParent(parent);
		}
		return departments;
	}

	@Override
	public void save(Department department) throws Exception {
		departmentMapper.insertSelective(department);
	}

	@Override
	public void delete(Long id) throws Exception {
		departmentMapper.deleteByPrimaryKey(id);
	}

	@Override
	public Department getById(Long id) throws Exception {
		Department department = departmentMapper.selectByPrimaryKey(id);
		if (department == null) {
			return null;
		}
		Department parent = departmentMapper.selectByPrimaryKey(department.getParentId());
		department.setParent(parent);
		return department;
	}

	@Override
	public void update(Department department) throws Exception {
		departmentMapper.updateByPrimaryKey(department);
	}

	@Override
	public List<Department> findTopList() throws Exception {
		Example example = new Example(Department.class);
		Criteria criteria = example.createCriteria();
		criteria.andIsNull("parentId");
		List<Department> departments = departmentMapper.selectByExample(example);
		for (Department department : departments) {
			Department parent = departmentMapper.selectByPrimaryKey(department.getParentId());
			department.setParent(parent);
			List<Department> children = findChildren(department.getId());
			department.setChildren(children);
		}
		return departments;
	}

	@Override
	public List<Department> findChildren(Long parentId) throws Exception {
		Example example = new Example(Department.class);
		example.or().andEqualTo("parentId", parentId);
		List<Department> departments = departmentMapper.selectByExample(example);
		for (Department department : departments) {
			Department parent = departmentMapper.selectByPrimaryKey(department.getParentId());
			department.setParent(parent);
			List<Department> children = findChildren(department.getId());
			department.setChildren(children);
		}
		return departments;
	}
	
}
