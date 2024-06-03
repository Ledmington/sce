#[derive(Debug)]
pub enum Token<'a> {
    Plus,
    Minus,
    Asterisk,
    Slash,
    Circumflex,
    LeftBracket,
    RightBracket,
    Name(&'a str),
    IntegerLiteral(i64),
}
