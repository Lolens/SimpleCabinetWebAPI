package com.gravitlauncher.simplecabinet.web.service.shop.group.delivery;

import com.gravitlauncher.simplecabinet.web.model.shop.GroupOrder;
import com.gravitlauncher.simplecabinet.web.model.shop.GroupProduct;
import com.gravitlauncher.simplecabinet.web.model.user.User;
import jakarta.persistence.*;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.time.ZoneOffset;
import java.util.List;
import java.util.Locale;
import java.util.UUID;

@Service("luckPermsDeliveryService")
public class LuckPermsDeliveryService implements GroupDeliveryService {
    @Value("${shop.group.luckperms.table}")
    private String table;
    @Value("${shop.group.luckperms.prefixWeight}")
    private String prefixWeight;
    @PersistenceContext
    private EntityManager manager;

    public void insertPermission(UUID uuid, String permission, boolean value, String server, String world, long timestampExpire, String context) {
        Query query = manager.createNativeQuery(String.format("INSERT INTO %s (\"uuid\",\"permission\",value,\"server\",world,expiry,contexts) " +
                " VALUES (?,?,?,?,?,?,?);", table));
        query.setParameter(1, uuid.toString());
        query.setParameter(2, permission);
        query.setParameter(3, value);
        query.setParameter(4, server);
        query.setParameter(5, world);
        query.setParameter(6, timestampExpire);
        query.setParameter(7, context);
        query.executeUpdate();
    }

    @SuppressWarnings("unchecked")
    public List<LuckPermsPermission> searchUserPermission(UUID uuid, String prefix, String server, String world) {
        Query query = manager.createNativeQuery(String.format("SELECT uuid, permission, value, server, world, expiry, contexts from %s where uuid = ? and permission like ? escape '\\' and server = ? and world = ? and (expiry = 0 or expiry > ?)", table),
                "LuckPermsPermissionMappings");

        query.setParameter(1, uuid.toString());
        query.setParameter(2, escapeLike(prefix, "\\") + "%");
        query.setParameter(3, server);
        query.setParameter(4, world);
        query.setParameter(5, LocalDateTime.now().toEpochSecond(ZoneOffset.UTC));
        return (List<LuckPermsPermission>) query.getResultList();
    }

    public int upUserPermission(UUID uuid, String prefix, String server, String world, long expiry) {
        Query query = manager.createNativeQuery(String.format("UPDATE %s SET expiry = ? where uuid = ? and permission like ? escape '\\' and server = ? and world = ? and (expiry != 0 or expiry > ?)", table));
        query.setParameter(1, expiry);
        query.setParameter(2, uuid.toString());
        query.setParameter(3, escapeLike(prefix, "\\") + "%");
        query.setParameter(4, server);
        query.setParameter(5, world);
        query.setParameter(6, LocalDateTime.now().toEpochSecond(ZoneOffset.UTC));
        return query.executeUpdate();
    }

    public boolean deletePermission(LuckPermsPermission permission) {
        return deletePermission(UUID.fromString(permission.uuid()), permission.permission(), permission.value(), permission.server(),
                permission.world(), permission.expiry(), permission.contexts());
    }

    public boolean deletePermission(UUID uuid, String permission, boolean value, String server, String world, long timestampExpire, String context) {
        Query query = manager.createNativeQuery(String.format("DELETE FROM %s where uuid = ? and permission = ? and value = ? and server = ? and world = ? and expiry = ? and contexts = ?", table));
        query.setParameter(1, uuid.toString());
        query.setParameter(2, permission);
        query.setParameter(3, value);
        query.setParameter(4, server);
        query.setParameter(5, world);
        query.setParameter(6, timestampExpire);
        query.setParameter(7, context);
        return query.executeUpdate() > 0;
    }

    private String escapeLike(String value, String escape) {
        return value.replace(escape, escape + escape).replace("%", escape + "%").replace("_", escape + "_");
    }

    @Override
    @Transactional
    public void delivery(GroupOrder order) {
        User user = order.getUser();
        GroupProduct product = order.getProduct();
        long expiry = product.getExpireDays() == 0 ? 0 : LocalDateTime.now().plusDays(product.getExpireDays() * order.getQuantity()).toEpochSecond(ZoneOffset.UTC);
        insertPermission(user.getUuid(), String.format("group.%s", product.getName().toLowerCase(Locale.ROOT)), true, product.getServer(), product.getWorld(),
                expiry,
                product.getContext());
    }

    @Override
    @Transactional
    public void updatePrefix(String prefix, UUID userUUID, LocalDateTime endDate) {
        var list = searchUserPermission(userUUID, "prefix." + prefixWeight + ".", "global", "global");
        if (!list.isEmpty()) {
            if (list.size() > 1) {
                throw new SecurityException("Found more than one prefix permission");
            }
            deletePermission(list.getFirst());
        }
        insertPermission(userUUID, "prefix." + prefixWeight + "." + prefix, true, "global", "global",
                endDate == null ? 0 : endDate.toEpochSecond(ZoneOffset.UTC), "{}");
    }

    @Override
    public boolean deletePrefix(UUID userUUID) {
        var list = searchUserPermission(userUUID, "prefix." + prefixWeight + ".", "global", "global");
        if (!list.isEmpty()) {
            if (list.size() > 1) {
                throw new SecurityException("Found more than one prefix permission");
            }
            return deletePermission(list.getFirst());
        }
        return false;
    }

    public record LuckPermsPermission(String uuid, String permission, boolean value, String server, String world,
                                      long expiry, String contexts) {
    }

    @SqlResultSetMapping(
            name = "LuckPermsPermissionMappings",
            classes = @ConstructorResult(
                    targetClass = LuckPermsPermission.class,
                    columns = {
                            @ColumnResult(name = "uuid", type = String.class),
                            @ColumnResult(name = "permission", type = String.class),
                            @ColumnResult(name = "value", type = boolean.class),
                            @ColumnResult(name = "server", type = String.class),
                            @ColumnResult(name = "world", type = String.class),
                            @ColumnResult(name = "expiry", type = long.class),
                            @ColumnResult(name = "contexts", type = String.class)
                    }
            )
    )
    @Entity
    public class LuckPermsDummyEntity {
        @Id
        private Long id;
    }
}
