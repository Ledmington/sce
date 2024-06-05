use crate::token;

pub fn tokenize(input: &str) -> Vec<token::Token> {
    let mut tokens: Vec<token::Token> = vec![];

    let mut i: usize = 0;
    while i < input.len() {
        let ch: char = input.chars().nth(i).unwrap();

        if ch.is_ascii_digit() {
            tokens.push(token::Token::IntegerLiteral(read_number(input, &mut i)));
        } else if ch.is_alphabetic() {
            tokens.push(token::Token::Name(read_name(input, &mut i).to_owned()));
        } else if ch == '+' {
            tokens.push(token::Token::Plus);
            i += 1;
        } else if ch == '-' {
            tokens.push(token::Token::Minus);
            i += 1;
        } else if ch == '*' {
            tokens.push(token::Token::Asterisk);
            i += 1;
        } else if ch == '/' {
            tokens.push(token::Token::Slash);
            i += 1;
        } else if ch == '^' {
            tokens.push(token::Token::Circumflex);
            i += 1;
        } else if ch == '(' {
            tokens.push(token::Token::LeftBracket);
            i += 1;
        } else if ch == ')' {
            tokens.push(token::Token::RightBracket);
            i += 1;
        } else {
            panic!("Invalid character: {}", input.chars().nth(i).unwrap());
        }
    }

    tokens
}

fn read_number(input: &str, i: &mut usize) -> i64 {
    let mut x: i64 = 0;

    while *i < input.len() && input.chars().nth(*i).unwrap().is_ascii_digit() {
        x = 10 * x + input.chars().nth(*i).unwrap().to_digit(10).unwrap() as i64;
        *i += 1;
    }

    x
}

fn read_name<'a>(input: &'a str, i: &mut usize) -> &'a str {
    let start: usize = *i;
    let mut end: usize = *i;

    while *i < input.len() && input.chars().nth(*i).unwrap().is_alphabetic() {
        end += 1;
        *i += 1;
    }

    &input[start..end]
}

#[cfg(test)]
mod tests {
    use super::*;
    use test_case::test_case;

    #[test_case("0", 0)]
    #[test_case("1", 1)]
    #[test_case("12", 12)]
    #[test_case("123", 123)]
    fn number_parsing(input: &str, output: i64) {
        assert_eq!(read_number(input, &mut 0), output);
    }
}
