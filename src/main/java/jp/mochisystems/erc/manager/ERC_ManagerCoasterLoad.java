//package erc.manager;
//
//import java.util.HashMap;
//import java.util.List;
//import java.util.Map;
//import java.util.TreeMap;
//import java.util.UUID;
//
//import erc._mc._1_7_10._core.ERC_Logger;
//import erc._mc._1_7_10.entity.EntityCoaster;
//import net.minecraft.entity.Entity;
//import net.minecraft.world.World;
//
////sideonly server
//public class ERC_ManagerCoasterLoad {
//
//	//�eMap�p
//	static class ParentConstructConnection{
//		EntityCoaster parent;
//		Map<Integer, EntityCoaster> childrenMap;
//		ParentConstructConnection(EntityCoaster p)
//		{
//			parent = p;
//			childrenMap = new TreeMap<Integer, EntityCoaster>();
//		}
//		public void addChildren(EntityCoaster child, int idx)
//		{
//			childrenMap.put(idx, child);
//		}
//	}
//
//	//�qMap�p
//	static class ConnectorCoasterCounter{
//		EntityCoaster coaster;
//		int counter = 40; //�e�����[�h����邩�ǂ����҂��ԁitick�j
//		public ConnectorCoasterCounter(EntityCoaster c){coaster = c;}
//	}
//
//	static Map<UUID, ParentConstructConnection> parentMap = new HashMap<UUID, ParentConstructConnection>();
//	static Map<Integer, ConnectorCoasterCounter>childMap = new HashMap<Integer, ConnectorCoasterCounter>();
//
//	//�e�����[�h���ꂽ�炱���ɓo�^
//	public static void registerParentCoaster(EntityCoaster parent)
//	{
//		if(parent.connectNum==0)return; //�q���Ȃ����Ă��Ȃ�������o�^���Ȃ�
//		parentMap.put(parent.getUniqueID(), new ParentConstructConnection(parent));
////		ERC_Logger.info("register manager: parent, num:"+parent.connectNum + " ... parentid:"+parent.getUniqueID().toString());
//	}
//	//�q�����[�h���ꂽ�炱���ɓo�^
//	public static void registerChildCoaster(EntityCoaster child)
//	{
//		childMap.put(child.getEntityId(), new ConnectorCoasterCounter(child));
//	}
//	//�q���e�͂��Ȃ����Ɗm�F���ɗ���
//	public static boolean searchParent(int childid, int idx, UUID parentid)
//	{
//		//�����e���������Ă���(Map�ɓ����Ă��Ȃ�)�q����̗v���͋p��
//		if(childMap.get(childid)==null)return false;
//		ParentConstructConnection parent = parentMap.get(parentid);
//		if(parent == null)
//		{
//			//�e��������񂩂���
//			ERC_Logger.info("find parent false");
//			ConnectorCoasterCounter ccc = childMap.get(childid);
////			ERC_Logger.info("CoasterManager countdown:"+(ccc.counter-1)+" parentid:"+parentid.toString());
//			if(--ccc.counter <= 0)
//			{
//				ccc.coaster.killCoaster(); // ��莞�Ԗ���������A�C�e����
//				childMap.remove(childid);
//			}
//			return false;
//		}
//		else
//		{
//			// �e����������J�E���g�f�N�������g�Ǝq�̓o�^
////			ERC_Logger.info("find parent, parentID:"+parent.parent.getEntityId()+"childID:"+childid+"num:"+parent.parent.connectNum+">"+(parent.parent.connectNum-1));
//			parent.childrenMap.put(idx, childMap.get(childid).coaster);
//			if(--parent.parent.connectNum<=0)
//			{
//				parent.parent.clearConnectCoaster();
//				for(Integer i : parent.childrenMap.keySet())
//				{
//					EntityCoaster c = parent.childrenMap.get(i);
//		            parent.parent.connectionCoaster(c);
//		        }
//				parentMap.remove(parentid); //�e�ɑS���q��������I���
////				ERC_Logger.info("connect all children");
//			}
//			// �q�͍폜
//			childMap.remove(childid);
//		}
//		return true;
//	}
//
//
//	public static Entity SearchEntityWithUUID(World world, UUID uuid)
//	{
//		@SuppressWarnings("unchecked")
//		List<Entity> elist = world.getLoadedEntityList();
//		for(Entity e :elist)
//		{
//			if(e.getUniqueID().equals(uuid))
//				return e;
//		}
//		return null;
//	}
//}
