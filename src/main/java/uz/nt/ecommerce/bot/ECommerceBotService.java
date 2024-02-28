package uz.nt.ecommerce.bot;

import lombok.RequiredArgsConstructor;
import lombok.SneakyThrows;
import org.springframework.stereotype.Service;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.api.methods.updatingmessages.EditMessageText;
import org.telegram.telegrambots.meta.api.objects.Contact;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.InlineKeyboardMarkup;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.ReplyKeyboardMarkup;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.ReplyKeyboardRemove;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.buttons.InlineKeyboardButton;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.buttons.KeyboardButton;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.buttons.KeyboardRow;
import uz.nt.ecommerce.domain.dto.BaseResponse;
import uz.nt.ecommerce.domain.dto.request.UserRequest;
import uz.nt.ecommerce.domain.dto.request.UserStateDto;
import uz.nt.ecommerce.domain.entity.BasketEntity;
import uz.nt.ecommerce.domain.entity.CategoryEntity;
import uz.nt.ecommerce.domain.entity.OrderEntity;
import uz.nt.ecommerce.domain.entity.ProductEntity;
import uz.nt.ecommerce.domain.entity.enums.UserRole;
import uz.nt.ecommerce.domain.entity.enums.UserState;
import uz.nt.ecommerce.repository.CategoryRepository;
import uz.nt.ecommerce.repository.ProductRepository;
import uz.nt.ecommerce.service.BasketService;
import uz.nt.ecommerce.service.OrderService;
import uz.nt.ecommerce.service.ProductService;
import uz.nt.ecommerce.service.UserService;

import java.util.ArrayList;
import java.util.List;

@Service
@RequiredArgsConstructor
public class ECommerceBotService {
    private final UserService userService;
    private final ProductRepository productRepository;
    private final ProductService productService;
    private final BasketService basketService;
    private final OrderService orderService;
    private final CategoryRepository categoryRepository;

    public SendMessage registerUser(String chatId, Contact contact) {
        SendMessage sendMessage = new SendMessage();
        sendMessage.setChatId(chatId);
        UserRequest userCreateDTO = UserRequest.builder()
                .roles(List.of(UserRole.USER))
                .password(contact.getPhoneNumber().substring(9))
                .username(contact.getFirstName())
                .name(contact.getFirstName())
                .phoneNumber(contact.getPhoneNumber())
                .balance(1000000.0)
                .chatId(chatId)
                .build();
        BaseResponse response = userService.saveBot(userCreateDTO);
        System.out.println(response);

        if (response.getStatus() == 200) {
            userService.updateState(new UserStateDto(chatId, UserState.REGISTERED));
            sendMessage.setReplyMarkup(mainMenu());
        }
        sendMessage.setText(response.getMessage());
        return sendMessage;
    }

    public UserState checkState(String chatId) {
        BaseResponse<UserStateDto> response = userService.getUserState(chatId);
        if (response.getStatus() == 200) {
            return response.getData().state();
        }
        return UserState.START;
    }

    public SendMessage shareContact(String chatId) {
        SendMessage sendMessage = new SendMessage(chatId, "*PLEASE REGISTER*");
        sendMessage.setReplyMarkup(requestContact());
        sendMessage.setParseMode("Markdown");
        return sendMessage;
    }

    public ReplyKeyboardMarkup requestContact() {
        ReplyKeyboardMarkup markup = new ReplyKeyboardMarkup();
        KeyboardRow keyboardRow = new KeyboardRow();
        KeyboardButton button = new KeyboardButton("PLEASE SHARE PHONE NUMBER 📞");
        button.setRequestContact(true);
        keyboardRow.add(button);

        markup.setResizeKeyboard(true);
        markup.setKeyboard(List.of(keyboardRow));
        return markup;
    }

    public SendMessage showMainMenu(String chatId) {
        SendMessage sendMessage = new SendMessage();
        sendMessage.setChatId(chatId);
        sendMessage.setText("*PLEASE CHOOSE ONE OPTION* \uD83D\uDD79️");
        sendMessage.setReplyMarkup(mainMenu());
        sendMessage.setParseMode("Markdown");
        return sendMessage;
    }

    public SendMessage chooseProductType(String chatId) {
        SendMessage sendMessage = new SendMessage();
        sendMessage.setChatId(chatId);
        sendMessage.setText(":::: *CHOOSE PRODUCT TYPE* ::::");
        sendMessage.setReplyMarkup(productType());
        sendMessage.setParseMode("Markdown");
        return sendMessage;
    }

    public InlineKeyboardMarkup productType() {
        InlineKeyboardMarkup inlineKeyboardMarkup = new InlineKeyboardMarkup();
        List<List<InlineKeyboardButton>> rows = new ArrayList<>();
        List<InlineKeyboardButton> row = new ArrayList<>();
        InlineKeyboardButton button = new InlineKeyboardButton();
        for (CategoryEntity category : categoryRepository.findAll()) {
            if (category.getParentId() == null) {
                button = new InlineKeyboardButton();
                button.setText(category.getName());
                button.setCallbackData(category.getId().toString());
                row.add(button);
                rows.add(row);
                row = new ArrayList<>();
            }
        }
        inlineKeyboardMarkup.setKeyboard(rows);
        return inlineKeyboardMarkup;
    }

    public ReplyKeyboardMarkup mainMenu() {
        ReplyKeyboardMarkup replyKeyboardMarkup = new ReplyKeyboardMarkup();

        List<KeyboardRow> keyboardRows = new ArrayList<>();

        KeyboardRow row = new KeyboardRow();
        row.add("\uD83D\uDCCB Categories");
        keyboardRows.add(row);

        row = new KeyboardRow();
        row.add("\uD83D\uDCD1 Orders");
        keyboardRows.add(row);

        row = new KeyboardRow();
        row.add("\uD83D\uDED2 Basket");
        keyboardRows.add(row);

        replyKeyboardMarkup.setKeyboard(keyboardRows);
        return replyKeyboardMarkup;
    }

    public ReplyKeyboardRemove keyboardRemove() {
        return new ReplyKeyboardRemove(true);
    }

    public SendMessage chooseProduct(String chatId) {
        SendMessage sendMessage = new SendMessage();
        sendMessage.setChatId(chatId);
        sendMessage.setText("*AVAILABLE PRODUCTS*");
        sendMessage.setReplyMarkup(chooseProduct());
        sendMessage.setParseMode("Markdown");
        return sendMessage;
    }

    @SneakyThrows
    public InlineKeyboardMarkup chooseProduct() {
        InlineKeyboardMarkup inlineKeyboardMarkup = new InlineKeyboardMarkup();
        List<List<InlineKeyboardButton>> rows = new ArrayList<>();
        List<InlineKeyboardButton> row = new ArrayList<>();
        InlineKeyboardButton button = new InlineKeyboardButton();
        for (ProductEntity product : productRepository.findAll()) {
            button = new InlineKeyboardButton();
            button.setText(product.getName());
            button.setCallbackData(product.getId().toString());
            row.add(button);
            rows.add(row);
            row = new ArrayList<>();
        }
        inlineKeyboardMarkup.setKeyboard(rows);
        return inlineKeyboardMarkup;
    }

    public EditMessageText confirmUpdate(String chatId, int messageId, ProductEntity product) {
        EditMessageText edit = new EditMessageText();
        edit.setChatId(chatId);
        edit.setMessageId(messageId);
        edit.setText("YOU HAVE CHOSEN: " + product.getName() +
                "\nCURRENT PRICE: " + product.getPrice() +
                "\nCURRENT AMOUNT: " + product.getQuantity() +
                "\nDO YOU REALLY WANT TO EDIT THIS PRODUCT?");
        edit.setReplyMarkup(confirmUpdate());
        return edit;
    }

    public InlineKeyboardMarkup confirmUpdate() {
        InlineKeyboardMarkup inlineKeyboardMarkup = new InlineKeyboardMarkup();
        List<List<InlineKeyboardButton>> rows = new ArrayList<>();
        List<InlineKeyboardButton> row = new ArrayList<>();

        InlineKeyboardButton button = new InlineKeyboardButton();
        button.setText("YES ☑");
        button.setCallbackData("yes");
        row.add(button);

        button = new InlineKeyboardButton();
        button.setText("NO ✖");
        button.setCallbackData("no");
        row.add(button);

        rows.add(row);
        inlineKeyboardMarkup.setKeyboard(rows);
        return inlineKeyboardMarkup;
    }

    @SneakyThrows
    public InlineKeyboardMarkup chooseCategoryChild(CategoryEntity type) {
        InlineKeyboardMarkup inlineKeyboardMarkup = new InlineKeyboardMarkup();
        List<List<InlineKeyboardButton>> rows = new ArrayList<>();
        List<InlineKeyboardButton> row = new ArrayList<>();
        InlineKeyboardButton button = new InlineKeyboardButton();
        List<CategoryEntity> categoryEntities = categoryRepository.findAll();
        for (CategoryEntity category : categoryEntities) {
            if (category.getParentId() != null && category.getParentId().equals(type.getId())) {
                button = new InlineKeyboardButton();
                button.setText(category.getName());
                button.setCallbackData(category.getId().toString());
                row.add(button);
                rows.add(row);
                row = new ArrayList<>();
            }
        }

        rows.add(row);
        inlineKeyboardMarkup.setKeyboard(rows);
        return inlineKeyboardMarkup;
    }

    @SneakyThrows
    public InlineKeyboardMarkup chooseCategoryProduct(CategoryEntity type) {
        InlineKeyboardMarkup inlineKeyboardMarkup = new InlineKeyboardMarkup();
        List<List<InlineKeyboardButton>> rows = new ArrayList<>();
        List<InlineKeyboardButton> row = new ArrayList<>();
        InlineKeyboardButton button = new InlineKeyboardButton();
        List<ProductEntity> productEntities = productRepository.findAll();
        for (ProductEntity product : productEntities) {
            if (product.getCategory().getId() != null && product.getCategory().getId().equals(type.getId())) {
                button = new InlineKeyboardButton();
                button.setText(product.getName());
                button.setCallbackData(product.getId().toString());
                row.add(button);
                rows.add(row);
                row = new ArrayList<>();
            }
        }

        rows.add(row);
        inlineKeyboardMarkup.setKeyboard(rows);
        return inlineKeyboardMarkup;
    }

    public EditMessageText basketAmount(String chatId, int messageId, ProductEntity product) {
        EditMessageText edit = new EditMessageText();
        edit.setChatId(chatId);
        edit.setMessageId(messageId);
        edit.setText("YOU HAVE CHOSEN: " + product.getName() +
                "\nPRODUCT PRICE: " + product.getPrice() +
                "\nAMOUNT AVAILABLE: " + product.getQuantity() +
                "\nHOW MANY OF THIS PRODUCT YOU WANT TO ADD TO BASKET?");
        edit.setReplyMarkup(amountPurchase());
        return edit;
    }

    public EditMessageText confirmPurchase(String chatId, int messageId, ProductEntity product, int amount) {
        EditMessageText edit = new EditMessageText();
        edit.setChatId(chatId);
        edit.setMessageId(messageId);
        edit.setText("YOU HAVE CHOSEN: " + product.getName() +
                "\nTOTAL PRICE: " + product.getPrice() * amount +
                "\nTOTAL AMOUNT: " + amount +
                "\nDO YOU WANT TO ADD THIS TO BASKET?");
        edit.setReplyMarkup(confirmUpdate());
        return edit;
    }

    public InlineKeyboardMarkup amountPurchase() {
        InlineKeyboardMarkup inlineKeyboardMarkup = new InlineKeyboardMarkup();
        List<List<InlineKeyboardButton>> rows = new ArrayList<>();
        List<InlineKeyboardButton> row = new ArrayList<>();

        InlineKeyboardButton button = new InlineKeyboardButton();
        button.setText("1");
        button.setCallbackData("1");
        row.add(button);

        button = new InlineKeyboardButton();
        button.setText("2");
        button.setCallbackData("2");
        row.add(button);

        button = new InlineKeyboardButton();
        button.setText("3");
        button.setCallbackData("3");
        row.add(button);

        rows.add(row);
        row = new ArrayList<>();
        button = new InlineKeyboardButton();
        button.setText("4");
        button.setCallbackData("4");
        row.add(button);

        button = new InlineKeyboardButton();
        button.setText("5");
        button.setCallbackData("5");
        row.add(button);

        button = new InlineKeyboardButton();
        button.setText("6");
        button.setCallbackData("6");
        row.add(button);

        rows.add(row);
        inlineKeyboardMarkup.setKeyboard(rows);
        return inlineKeyboardMarkup;
    }

    public SendMessage myBasket(String chatId, Long userId) {
        SendMessage sendMessage = new SendMessage();
        sendMessage.setChatId(chatId);
        sendMessage.setText("*:::YOUR BASKET:::*");
        sendMessage.setReplyMarkup(chooseProduct());
        sendMessage.setParseMode("Markdown");
        sendMessage.setReplyMarkup(basketInline(userId));
        return sendMessage;
    }

    @SneakyThrows
    public InlineKeyboardMarkup basketInline(Long userId) {
        InlineKeyboardMarkup inlineKeyboardMarkup = new InlineKeyboardMarkup();
        List<List<InlineKeyboardButton>> rows = new ArrayList<>();
        List<InlineKeyboardButton> row = new ArrayList<>();
        InlineKeyboardButton button;

        for (BasketEntity basket : basketService.getMyBaskets(userId)) {
            button = new InlineKeyboardButton();
            button.setText(productService.getById(basket.getProduct().getId()).getData().getName());
            button.setCallbackData(basket.getId().toString());
            row.add(button);
            rows.add(row);
            row = new ArrayList<>();
        }

        button = new InlineKeyboardButton();
        button.setText("GO BACK \uD83D\uDD19");
        button.setCallbackData("back");
        row.add(button);

        button = new InlineKeyboardButton();
        button.setText("REMOVE ALL \uD83D\uDEAB");
        button.setCallbackData("remove");
        row.add(button);

        button = new InlineKeyboardButton();
        button.setText("ORDER ALL \uD83D\uDCB0");
        button.setCallbackData("order");
        row.add(button);

        rows.add(row);
        inlineKeyboardMarkup.setKeyboard(rows);
        return inlineKeyboardMarkup;
    }

    public InlineKeyboardMarkup basketEdit() {
        InlineKeyboardMarkup inlineKeyboardMarkup = new InlineKeyboardMarkup();
        List<List<InlineKeyboardButton>> rows = new ArrayList<>();
        List<InlineKeyboardButton> row = new ArrayList<>();
        InlineKeyboardButton button = new InlineKeyboardButton();

        button.setText("GO BACK \uD83D\uDD19");
        button.setCallbackData("back edit");
        row.add(button);

        button = new InlineKeyboardButton();
        button.setText("REMOVE \uD83D\uDEAB");
        button.setCallbackData("remove basket");
        row.add(button);

        rows.add(row);
        inlineKeyboardMarkup.setKeyboard(rows);
        return inlineKeyboardMarkup;
    }

    @SneakyThrows
    public InlineKeyboardMarkup allOrders() {
        InlineKeyboardMarkup inlineKeyboardMarkup = new InlineKeyboardMarkup();
        List<List<InlineKeyboardButton>> rows = new ArrayList<>();
        List<InlineKeyboardButton> row = new ArrayList<>();
        InlineKeyboardButton button = new InlineKeyboardButton();

        for (OrderEntity order : orderService.getAll()) {
            button = new InlineKeyboardButton();
            button.setText(productService.getById(order.getProduct().getId()).getData().getName());
            button.setCallbackData(order.getId().toString());
            row.add(button);
            rows.add(row);
            row = new ArrayList<>();
        }

        rows.add(row);
        inlineKeyboardMarkup.setKeyboard(rows);
        return inlineKeyboardMarkup;
    }

    public SendMessage orderStatus(String chatId) {
        SendMessage sendMessage = new SendMessage();
        sendMessage.setChatId(chatId);
        sendMessage.setText("*:::CHOOSE ORDER:::*");
        sendMessage.setReplyMarkup(chooseProduct());
        sendMessage.setParseMode("Markdown");
        sendMessage.setReplyMarkup(allOrders());
        return sendMessage;
    }

}
