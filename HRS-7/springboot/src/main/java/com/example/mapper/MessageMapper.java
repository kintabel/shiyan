    package com.example.mapper;

    import com.example.entity.Message;
    import com.example.entity.User;
    import org.apache.ibatis.annotations.Mapper;
    import org.apache.ibatis.annotations.Param;
    import org.apache.ibatis.annotations.Select;

    import java.util.List;

    @Mapper
    public interface MessageMapper {
        int insertMessage(Message message);

        List<Message> selectConversation(
                @Param("user1Id") Long user1Id,
                @Param("user2Id") Long user2Id
        );


        List<User> selectPatients(Integer  doctorId);
    }